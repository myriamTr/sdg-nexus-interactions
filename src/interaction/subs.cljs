(ns interaction.subs
  [:require
   [interaction.db :refer [sdgs->targets]]
   [re-frame.core :as rf :refer (reg-sub)]
   [clojure.set]])

(reg-sub
 :active-tab
 (fn [db _]
   (:active-tab db)))

(reg-sub
 :heatmap-sdgs-polarity
 (fn [db _]
   (get db :heatmap-sdgs-polarity :positive)))

(reg-sub
 :heatmap-targets-polarity
 (fn [db _]
   (get db :heatmap-targets-polarity :positive)))

(reg-sub
 :interaction-data
 (fn [db _]
   (->>
    (get-in db [:data :interaction])
    (map #(update % "Score" js/parseInt)))))

(reg-sub
 :references-data
 (fn [db _]
   (->> (get-in db [:data :references]))))

(reg-sub
 :references-data-map
 :<- [:references-data]
 (fn [data _]
   (->> data
        (group-by (fn [m] (get m "title")))
        (reduce-kv #(assoc %1 %2 (first %3)) {}))))

(reg-sub
 :sdg-from
 (fn [db _] (:sdg-from db)))

(reg-sub
 :sdg-to
 (fn [db _] (:sdg-to db)))

(reg-sub
 :target
 (fn [db [_ id]]
   (get-in db [:target id])))

;; SDGS Level
(reg-sub
 :interaction-data-sdgs
 :<- [:interaction-data]
 (fn [data _]
   (map
    #(-> %
         (select-keys ["From SDG" "To SDG " "From target" "To target"  "Score"])
         (clojure.set/rename-keys
          {"From SDG" :sdg-from "To SDG " :sdg-to "From target" :target-from
           "To target" :target-to "Score" :score}))
    data)))

(reg-sub
 :interaction-data-sdgs-groupped
 :<- [:interaction-data-sdgs]
 (fn [data _]
   (group-by (juxt :sdg-from :sdg-to) data)))

(reg-sub
 :interaction-data-targets
 :<- [:interaction-data]
 (fn [data _]
   (let [f #(-> % (clojure.set/rename-keys
                   {"From SDG" :sdg-from "To SDG " :sdg-to "From target" :target-from
                    "To target" :target-to "Score" :score}))
         replace-empty (fn [m k v] (if (empty? (m k)) v (m k)))]
     (->> data
          (map f)
          (map (fn [m]
                 (assoc m
                        :target-to (replace-empty m :target-to (m :sdg-to))
                        :target-from (replace-empty m :target-from (m :sdg-from)))))
          (group-by (juxt :target-from :target-to))))))

(reg-sub
 :interaction-data-targets-pair->details
 :<- [:interaction-data-targets]
 (fn [m [_ k]] (->> (get m k) (sort-by #(get % "Title")))))

(reg-sub
 :interaction-data-sdgs-sum
 :<- [:interaction-data-sdgs-groupped]
 (fn [data [_ positive?]]
   (reduce-kv
    (fn [m k v]
      (let [w (filter #((if positive? pos? neg?) (:score %)) v)]
        (if (seq w) (assoc m k (reduce #(+ %1 (:score %2)) 0 w)) m))) {} data)))

(defn map-vals [f m]
  (reduce-kv (fn [m k v] (assoc m k (f v))) {} m))

(defn score-distribution [ms]
  (let [w (-> (group-by #(-> % :score pos?) ms)
             (clojure.set/rename-keys {true :positive false :negative}))]
    (map-vals #(->> % (map :score) (reduce +)) w)))

(reg-sub
 :interaction-data-sdgs-pie-sum
 :<- [:interaction-data-sdgs-groupped]
 (fn [data _]
   (->> data
       (reduce-kv
        (fn [m k v] (assoc m k (score-distribution v))) {})
       (map-vals #(update % :negative (partial * -1))))))

(reg-sub
 :interaction-data-sdgs-sum-plotly
 (fn [db [_ positive?]]
   [(rf/subscribe [:interaction-data-sdgs-sum true])
    (rf/subscribe [:interaction-data-sdgs-sum false])])
 (fn [[m-pos m-neg] [_ positive?]]
   (let [m (if positive? m-pos m-neg)
         max-sdg 17
         labels (map str (range 1 (inc max-sdg)))]
     (->> (for [from-sdg labels to-sdg labels]
            (get m [from-sdg to-sdg] 0))
          (partition max-sdg)
          vec
          reverse))))

(reg-sub
 :interaction-data-sdgs-count
 :<- [:interaction-data-sdgs-groupped]
 (fn [data [_ positive?]]
   (->> data
        (reduce-kv
         (fn [m k v]
           (let [w (filter #((if positive? pos? neg?) (:score %)) v)]
             (if (seq w) (assoc m k (count w)) m))) {}))))

(reg-sub
 :interaction-data-sdgs-count-plotly
 (fn [db [_ positive?]]
   [(rf/subscribe [:interaction-data-sdgs-count true])
    (rf/subscribe [:interaction-data-sdgs-count false])])
 (fn [[m-pos m-neg] [_ positive?]]
   (let [m (if positive? m-pos m-neg)
         max-sdg 17
         labels (map str (range 1 (inc max-sdg)))]
     (->> (for [from-sdg labels to-sdg labels]
            (get m [from-sdg to-sdg] 0))
          (partition max-sdg)
          vec))))

;; target level
(reg-sub
 :interaction-data-sdgs-targets
 :<- [:interaction-data-sdgs-groupped]
 (fn [data _]
   (reduce-kv
    (fn [m k v]
      (let [w (group-by (juxt :target-from :target-to) v)
            scores (->> (map-vals score-distribution w)
                        (map-vals #(update % :negative (partial * -1))))]
        (assoc m k scores))) {} data)))

;; ;; SDGS Interaction targets
(reg-sub
 :interaction-data-targets-sum
 :<- [:interaction-data-sdgs-targets]
 (fn [data [_ positive?]]
   (let [reduce-fn
         (fn [m k v]
           (let [w (filter #((if positive? pos? neg?) (:score %)) v)]
             (if (seq w) (assoc m k (reduce #(+ %1 (:score %2)) 0 w) m))))]
     (reduce-kv
      (fn [m k v]
        (assoc m k (reduce-kv reduce-fn {} v)))
      {} data))))

(reg-sub
 :interaction-data-targets-sum-plotly
 (fn [db [_ positive? from-sdg to-sdg]]
   [(rf/subscribe [:interaction-data-targets-sum true])
    (rf/subscribe [:interaction-data-targets-sum false])])
 (fn [[m-pos m-neg] [_ positive? from-sdg to-sdg]]
   (when (and from-sdg to-sdg)
     (let [m (if positive? m-pos m-neg)
           generate-targets
           (fn [x] (for [i (range 1 (-> x sdgs->targets :targets inc))]
                     (str x "." i)))
           from-targets (generate-targets from-sdg)
           to-targets (generate-targets to-sdg)]
       (->> (for [x from-targets y to-targets]
              (get-in m [[(str from-sdg) (str to-sdg)] [x y]] 0))
            (partition (-> to-sdg sdgs->targets :targets))
            vec
            reverse)))))


(comment
  (def x @(rf/subscribe [:interaction-data-targets-pair->details ["11.2" "1.2"]])))
