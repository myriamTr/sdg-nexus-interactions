(ns interaction.charts.pie
  (:require
   [interaction.db :refer [sdgs->targets id->title]]
   [cljs.pprint :as pprint]
   [clojure.string :as str]
   [reagent.core :as r]
   [re-frame.core :as rf :refer (subscribe dispatch)]
   [bulma-cljs.core :as b]
   ["react-plotly.js" :default react-plotly]))

(def chart-colors (reverse ["#337BAE" "#FD3C3C"]))

(def plotly-common-args
  {:layout {:showlegend true
            :autosize true
            :margin {:t 50 :b 50}
            :title {:x 0.05 :xref :paper :y 1.2
                    :font {:size 24 :color "#7f7f7f"}}
            :grid {:rows 18 :columns 18}}
   :style {:width 1200 :height 1080}
   :useResizeHandler true
   :config {:toImageButtonOptions
            {:format "png" :height 960 :width 1080 :scale 2.5}
            :editable false}})

(defn sdg->icon-path
  ([sdg] (sdg->icon-path sdg nil))
  ([sdg target]
   ;; examples (sdg->icon-path "3" "3.5") or (sdg->icon-path 3 nil)
   (let [target? (not (nil? target))]
     (str "/images/goal-" sdg
          "/"
          "GOAL_" sdg "_"
          (if-not target? "PRIMARY_ICON" "TARGETS")
          "/"
          "GOAL_" sdg "_" (when target? "TARGETS_") "PNG"
          "/"
          (if-not target?
            (str "TheGlobalGoals_Icons_Color_Goal_" sdg ".png")
            (str "GOAL_" sdg "_TARGET_" target "_SQUARE.png"))))))

(defn sdg->domain [s] (dec (js/parseInt s)))

(defn sdgs->trace
  ([v m] (sdgs->trace v m [10 30]))
  ([[sdg-from sdg-to] m bins]
   (let [values ((juxt :positive :negative) m)
         total (reduce + values)
         icon-html " &#x2B95; "
         trace-hover-name (str sdg-from icon-html sdg-to)]
     {:values (reverse values)
      :type :pie
      :labels (reverse ["Co-benefits" "Trade-offs"])
      :textinfo "none"
      :domain  {:row (-> sdg-from sdg->domain inc)
                :column (-> sdg-to sdg->domain  inc)}
      :name trace-hover-name
      :hole (cond (<= total (first bins)) 0.7 (<= total (second bins)) 0.5
                  :else 0.3)
      :hovertemplate
      (str "<b>" trace-hover-name "</b>"
           "<br>%{label}<br>Weight: %{value}<br>%{percent}<br>" "<extra></extra>")
      :opacity (cond (<= total (first bins)) 0.3 (<= total (second bins)) 0.7
                     :else 1)
      :sort false
      :marker {:colors chart-colors}})))

(defn trace-hover [target delta direction sdg+target]
  {:values [100]
   :type :pie
   :textinfo "none"
   :legend "false"
   :marker {:colors ["rgba(255, 255, 255, 1)"]}
   :labels "Hover"
   :name sdg+target
   :text [(id->title sdg+target)]
   :showlegend false
   :hovertemplate (->>
                   (str/split (str (id->title sdg+target) ".") #"\s")
                   (partition-all 5)
                   (mapv #(clojure.string/join " " %))
                   (interpose "<br>")
                   flatten
                   (clojure.string/join " ")
                   (str "<extra></extra>"))

   :domain (case direction
             :to {:row 0 :column target}
             :from {:row target :column 0})})

(defn pie [data polarity]
  (let [traces (reduce-kv (fn [m k v] (assoc m k (sdgs->trace k v))) {} data)
        on-click
        (fn [x]
          (let [point (-> x .-points first)
                [row column] ((juxt #(.-row %) #(.-column %)) (.. point -data -domain))
                [sdg-from sdg-to] [row column]]
            (rf/dispatch [:set-active-tab :targets-pie])
            (rf/dispatch [:select-sdg-from sdg-from])
            (rf/dispatch [:select-sdg-to sdg-to])))
        image-map {:xref :paper :yref :paper :xanchor :left :yanchor :top
                   :sizex (/ 1 18) :sizey (/ 1 18)}
        trace-hovers
        (into
         (mapv #(trace-hover % (/ 1 18) :from (str %)) (range 1 18))
         (mapv #(trace-hover % (/ 1 18) :to (str %)) (range 1 18)))]
    [:<>
     [:> react-plotly
      (-> plotly-common-args
          (assoc-in [:layout :title :text] "SDG-level interactions")
          (assoc-in [:layout :xaxis] {:title {:text "From SDG"} :type :category})
          (assoc-in [:layout :yaxis] {:title {:text "To SDG"} :type :category})
          (assoc-in
           [:layout :images]
           (reduce
            #(into %1 %2) []
            [[(assoc image-map :source "images/from_to.png" :x -0.002 :y 1.002)]
             (mapv #(assoc image-map :source (sdg->icon-path %) :x -0.002
                           :y (- 1 (* % (/ 1 18))))
                   (range 1 18))
             (mapv #(assoc image-map :source (sdg->icon-path %)
                           :x (* % (/ 1 18)) :y 1.002)
                   (range 1 18))]))
          (assoc-in [:data] (into (vals traces) trace-hovers))
          (assoc :onClick on-click))]]))

(defn target->domain [s] (-> s (clojure.string/split #"\.") last js/parseInt dec))


(defn target->trace
  ([v m] (target->trace v m [2 4]))
  ([[target-from target-to sdg-from sdg-to] m bins]
   (let [values ((juxt :positive :negative) m)
         total (reduce + values)
         iinc (partial + 2)
         trace-name (if-not (= "" target-from target-to)
                (str target-from "->" target-to)
                (str sdg-from "->" sdg-to))
         icon-html " &#x2B95; "
         trace-hover-name (if-not (= "" target-from target-to)
                       (str target-from icon-html target-to)
                       (str sdg-from icon-html sdg-to))]

     {:values (reverse values)
      :type :pie
      :labels (reverse ["Co-benefits" "Trade-offs"])
      :textinfo "none"
      :domain
      {:row (let [v (target->domain target-from)]
              (if-not (js/isNaN v)
                (inc v)
                0 #_ (inc (-> sdg-from sdgs->targets :targets))))
       :column (let [v (target->domain target-to)]
                 (if-not (js/isNaN v) (inc v) 0))}
      :name trace-name
      :hovertemplate
      (str "<b>" trace-hover-name "</b>"
           "<br>%{label}<br>Weight: %{value}<br>%{percent}<br>" "<extra></extra>")
      :hole (cond (<= total (first bins)) 0.7 (<= total (second bins)) 0.5
                  :else 0.3)
      :opacity (cond (<= total (first bins)) 0.3 (<= total (second bins)) 0.7
                     :else 1)
      :sort false
      :marker {:colors chart-colors}})))


(defn pie-target [data [sdg-from sdg-to]]
  (let [iinc (partial + 2)
        traces
        (reduce-kv
         #(assoc %1 %2 (target->trace (into %2 [sdg-from sdg-to]) %3))
         {} data)
        count-targets-from (-> sdg-from sdgs->targets :targets)
        count-targets-to (-> sdg-to sdgs->targets :targets)
        size-icon (let [m (min count-targets-from count-targets-to)]
                    (condp = m
                      count-targets-from (/ 1 (iinc m))
                      count-targets-to (/ 1 (inc m))))
        image-map {:xref :paper :yref :paper :xanchor :left :yanchor :top
                   :sizex size-icon :sizey size-icon}
        on-click
        (fn [x]
          (.log js/console x)
          (let [point (-> x .-points first)
                [target-from target-to] (-> (.. point -fullData -name)
                    (clojure.string/split #"->"))]
            (.log js/console target-from target-to point)
            (rf/dispatch [:set-active-tab :targets-details])
            (rf/dispatch [:select-target :from target-from])
            (rf/dispatch [:select-target :to target-to])))
        trace-hovers
        (into
         (mapv #(trace-hover % (/ 1 (+ 2 count-targets-from))
                             :from (str sdg-from "." %))
               (range 1 (inc count-targets-from)))
         (mapv #(trace-hover % (/ 1 (+ 1 count-targets-to))
                             :to (str sdg-to "." %))
               (range 1 (inc count-targets-to))))]
    [:<>
     [:> react-plotly
      (-> plotly-common-args
          (assoc-in [:config :toImageButtonOptions]
                    {:format "png" :height (+ 256 (* 108 count-targets-from))
                     :width (+ 256 (* 108 count-targets-to)) :scale 2.5})
          (assoc-in [:layout :grid]
                    {:rows (-> count-targets-from (+ 2))
                     :columns (+ 1 count-targets-to)})
          (assoc-in [:layout :title :text] "Target-level interactions")
          (assoc-in [:layout :margin] {:l 0 :b 0 :t 60 :r 0})
          (assoc-in [:style] {:width (+ 200 (* 100 (inc count-targets-to)))
                              :height (+ 110 (* 100 (iinc count-targets-from)))})
          (assoc-in
           [:layout :images]
           (into
            (mapv #(assoc image-map
                          :source (sdg->icon-path sdg-from (str sdg-from "." %))
                          :x -0.005
                          :y (- 0.995 (* % (/ 1 (+ 2 count-targets-from)))))
                  (range 1 (inc count-targets-from)))
            (mapv #(assoc image-map
                          :source (sdg->icon-path sdg-to (str sdg-to "." %))
                          :x (* % (/ 1 (+ 1 count-targets-to)))
                          :y 1.015)
                  (range 1 (inc count-targets-to)))))
          (assoc-in [:data] (into (vals traces) trace-hovers))
          (assoc :onClick on-click))]]))
