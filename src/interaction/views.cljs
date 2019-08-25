(ns interaction.views
  [:require
   [interaction.db :refer [sdgs->targets]]
   [interaction.charts.pie :refer [pie-target pie]]
   [cljs.pprint :as pprint]
   [clojure.string :as str]
   [reagent.core :as r]
   [reagent.debug :as rd]
   [re-frame.core :as rf]
   [bulma-cljs.core :as b]
   ["react-plotly.js" :default react-plotly]])

(def plotly-common-args
  {:layout {:showlegend true
            :autosize true
            :margin {:t 150 :b 50}
            :title {:x 0.05 :xref :paper :y 1.2
                    :font {:size 24 :color "#7f7f7f"}}}
   :style {:width 1080 :height 960}
   :useResizeHandler true
   :config {:toImageButtonOptions
            {:format "png" :height 560 :width 560 :scale 5}
            :editable false}})

(defn radio [name label checked? event]
  [:label.radio {:style {:margin-right 5 :margin-left 5}}
   [:input {:type "radio" :name name :value (-> label str/lower-case)
            :checked checked? :style {:margin-right 5}
            :on-click #(rf/dispatch (conj event (keyword (.. % -target -value))))}]
   label])

(defn radio-sdgs [name label checked?]
  [radio name label checked? [:choose-sdgs-polarity]])

(defn select-sdgs-polarity []
  (let [polarity (rf/subscribe [:heatmap-sdgs-polarity])]
    (fn []
      [:div.control
       [radio-sdgs "polarity" "Positive" (= @polarity :positive)]
       [radio-sdgs "polarity" "Negative" (= @polarity :negative)]])))

(defn radio-targets [name label checked?]
  [radio name label checked? [:choose-targets-polarity]])

(defn select-targets-polarity []
  (let [polarity (rf/subscribe [:heatmap-targets-polarity])]
    (fn []
      [:div.control
       [radio-targets "polarity" "Positive" (= @polarity :positive)]
       [radio-targets "polarity" "Negative" (= @polarity :negative)]])))

(defn input-field [label input-props]
  [:div.field
   [:label.label label]
   [b/input input-props]])

(defn axis
  ([title]
   (axis title false))
  ([title reversed?]
   {:type :category :title {:text title}}))

(defn heatmap-sdgs []
  (let [heatmap-sdgs-polarity (rf/subscribe [:heatmap-sdgs-polarity])]
    (fn []
      (let [positive? (= @heatmap-sdgs-polarity :positive)
            data @(rf/subscribe [:interaction-data-sdgs-sum-plotly positive?])
            labels (map str (range 1 18))
            polarity (rf/subscribe [:heatmap-sdgs-polarity])
            on-click
            (fn [x]
              (let [point (-> x .-points first)
                    [sdg-to sdg-from] ((juxt #(.-x %) #(.-y %)) point)]
                (rf/dispatch [:choose-targets-polarity @polarity])
                (rf/dispatch [:set-active-tab :targets])
                (rf/dispatch [:select-sdg-from sdg-from])
                (rf/dispatch [:select-sdg-to sdg-to])))]
        [:<>
         [:> react-plotly
          (-> plotly-common-args
              (assoc-in [:layout :title :text] "SDGS")
              (assoc :data
                     [{:z data :x labels :y (reverse labels) :type "heatmap"
                       :colorscale (if positive? "Blues" "Reds")
                       :reversescale true
                       :hovertemplate "From: %{y}<br>To: %{x}<br>Sum: %{z}<extra></extra>"}])
              (update-in [:layout] assoc
                         :xaxis (merge {:side :top} (axis "To SDG"))
                         :yaxis (axis "From SDG" true))
              (assoc :onClick on-click))]
         [select-sdgs-polarity]]))))

(defn select-sdgs-for-targets [sdg-from sdg-to]
  (let [s-from (r/atom sdg-from)
        s-to (r/atom sdg-to)]
    (fn [sdg-from sdg-to]
      [b/columns
       [b/column {:class :is-half}
        [input-field "From SGD"
         {:value @s-from
          :placeholder "From SGD"
          :on-change #(reset! s-from (-> % .-target .-value))
          :on-blur #(rf/dispatch [:select-sdg-from (-> % .-target .-value)])}]]
       [b/column {:class :is-half}
        [input-field "To SDG"
         {:value @s-to
          :placeholder "Target SGD"
          :on-change #(reset! s-to (-> % .-target .-value))
          :on-blur #(rf/dispatch [:select-sdg-to (-> % .-target .-value)])}]]])))

(defn select-sdgs-for-targets-container []
  (let [sdg-from (rf/subscribe [:sdg-from])
        sdg-to (rf/subscribe [:sdg-to])]
    (fn []
      [select-sdgs-for-targets @sdg-from @sdg-to])))

(defn generate-targets [x]
  (mapv #(str x "." %) (range 1 (-> x sdgs->targets :targets inc))))

(defn heatmap-targets []
  (let [heatmap-targets-polarity (rf/subscribe [:heatmap-targets-polarity])]
    (fn []
      (let [positive? (= @heatmap-targets-polarity :positive)
            sdg-from @(rf/subscribe [:sdg-from])
            sdg-to @(rf/subscribe [:sdg-to])
            labels-x (generate-targets sdg-to)
            labels-y (-> sdg-from generate-targets reverse)
            data @(rf/subscribe [:interaction-data-targets-sum-plotly
                                 positive? sdg-from sdg-to])
            hover-template
            (str "From .%{y}<br>To " "%{x}<br>Sum: %{z}<extra></extra>")
            on-click
            (fn [x]
              (let [point (-> x .-points first)
                    [target-to target-from] ((juxt #(.-x %) #(.-y %)) point)]
                (rf/dispatch [:set-active-tab :targets-details])
                (rf/dispatch [:select-target :from target-from])
                (rf/dispatch [:select-target :to target-to])));; [:select-target :from]
            ]
        [:<>
         (when (and labels-x labels-y data)
           [:> react-plotly
            (-> plotly-common-args
                (assoc-in [:layout :title :text] "Targets")
                (assoc :data
                       [{:z data
                         :x labels-x
                         :y labels-y
                         :type "heatmap"
                         :colorscale (if positive? "Blues" "Reds")
                         :reversescale true
                         :hovertemplate hover-template}])
                (update-in [:layout] assoc
                           :xaxis (merge {:side :top} (axis "To Target"))
                           :yaxis (axis "From Target" true))
                (assoc :onClick on-click))])
         [:br]
         [select-targets-polarity]
         [select-sdgs-for-targets-container]]))))

(defn pie-score-distribution-sdg []
  (let [data (rf/subscribe [:interaction-data-sdgs-pie-sum])]
    (fn [] [:<> [pie @data :positive]])))

(defn pie-score-distribution-target []
  (let [data (rf/subscribe [:interaction-data-sdgs-targets])
        sdg-from (rf/subscribe [:sdg-from])
        sdg-to (rf/subscribe [:sdg-to])]
    (fn []
      (let [sdg-link [@sdg-from @sdg-to]]
        [:<>
         [select-sdgs-for-targets-container]
         [pie-target (get @data (mapv str sdg-link)) sdg-link]]))))

(defn select-targets [target-from target-to id label]
  [b/columns
   [b/column {:class :is-half}
    [input-field "From Target"
     {:value (or target-from "")
      :placeholder "Target (.e.g 7.1)"
      :on-change #(rf/dispatch [:select-target :from (-> % .-target .-value)])}]]
   [b/column {:class :is-half}
    [input-field "To Target"
     {:value (or target-to "")
      :placeholder "Target (.e.g 7.1)"
      :on-change #(rf/dispatch [:select-target :to (-> % .-target .-value)])}]]])

(defn get-targets []
  {:target-from (rf/subscribe [:target :from])
   :target-to  (rf/subscribe [:target :to])})

(defn select-target-container []
  (let [{:keys [target-from target-to]} (get-targets)]
    (fn []
      [:<>
       [select-targets @target-from @target-to]])))

(defn target-card-detail-modal [citation]
  (let [activate (r/atom false)
        toggle #(swap! activate not)
        close #(reset! activate false)]
    (fn [citation]
      [:<>
       [:button.button.is-text
        {:on-click
         (fn []
           (.log js/console "Click")
           (rd/log @activate)
           (toggle)
           (rd/log @activate))} [:i.fas.fa-bookmark]]
       [:div.modal {:class (when @activate "is-active")}
        [:div.modal-background
         {:style {:background-color "rgba(0, 0, 0, 0.5)"}
          :on-click toggle}]
        [:div.modal-content
         [b/card {:style {:border-radius 2}}
          [b/card-content citation]
          [:button.button
           {:class ["modal-close" "is-large"]
            :on-click toggle}]]]]])))

(defn multiple-authors [authors citation]
  (if (and (> (count authors) 20) (re-find #"," (or authors "")) citation)
    (let [main-author (->> (str/split citation #"," 3)
                           (take 2)
                           (str/join " "))]
      (str main-author " et al."))
    authors))

(defn target-card-detail [m]

  (let [reference-data @(rf/subscribe [:references-data-map])
        positive-color (fn [a] (str "rgba(51,123,174, " a ")"))
        negative-color (fn [a] (str "rgba(253,60,60," a " )"))
        title (get m "Title")
        card-color (if (pos? (:score m)) positive-color negative-color)
        citation-chicago (get-in reference-data [title "citation"])
        title-corrected
        (let [s (get-in reference-data [title "title_corrected"])]
          (rd/log s)
          (if (seq s) s title))]

    [b/column {:class :is-half}
     [b/card
      {:style {:height "100%"
               :border-width 2
               :border-radius 5
               :border-style :solid
               :border-color (card-color 1)
               :background-color (card-color 0.2)}}
      [b/card-header
       [b/title
        [:div {:style {:padding 20}}
         [:a {:href (get-in reference-data [title "link"])
              :style {:color (card-color 0.8)}
              :target "_blank"} title-corrected]]]
       [:a {:class "card-header-icon"
            :target "_blank"
            :href (get-in reference-data [title "link"])}
        [:span.icon
         [:i {:style {:color (card-color 1)}
              :class "fas fa-external-link-alt"}]]]]
      [:div.card-content {:style {:padding 24}}
       [:div {:style {:display :flex :justify-content :space-between
                      :align-items :center}}
        [b/subtitle
         (str (multiple-authors (m "Author") citation-chicago) ". " (m "Year")
              (when-not (empty? (m "p.")) (str ". (p. " (m "p.") ")")))]
        [target-card-detail-modal (get-in reference-data [title "citation"])]]
       [:div {:style {:padding-top 10}}
        [:b
         (:target-from m)
         [:i.fas.fa-arrow-right {:style {:padding-left 10 :padding-right 10}}]
         (:target-to m)]
        [:br]
        [:b "ICSU Score: " (m "ICSU scale assessment")]]
       [:div {:style {:padding-top 20}}
        [:i (get m "Key insight")]]
       (let [material (get m "Further material:")]
         (when-not (empty? material)
           [:<> [:br]
            [:div "Further: " material]]))]]]))

(defn target-details []
  (let [{:keys [target-from target-to]} (get-targets)
        reference-data @(rf/subscribe [:references-data])]
    (when-not (seq reference-data)
      (rf/dispatch [:request-references-data]))
    (fn []
      (let [data @(rf/subscribe
                   [:interaction-data-targets-pair->details
                    [@target-from @target-to]])
            data-clean
            (->> data
                 (group-by #(vector (get % "Title") (get % "Key insight")))
                 vals
                 (mapv
                  (fn [xs]
                    ;; accumulate pages, then keep only distinct and reformat
                    (-> #(update %1 "p." (fn [s] (str s ", " (get %2 "p."))))
                        (reduce xs)
                        (update
                         "p."
                         (fn [s] (->> (str/split s #",\s*")
                                      sort
                                      distinct
                                      vec
                                      (str/join ", "))))))))
            data-pair (partition 2 2 nil data-clean)]
        [:<>
         [select-target-container]
         (for [[i xs] (zipmap (range) data-pair)]
           ^{:key i}
           [b/columns
            (for [[j m] (zipmap (range) xs) :when m]
              ^{:key (str i "." j)}
              [target-card-detail m])])]))))

(defn tab [key label active-tab]
  [:li {:class (when (= active-tab key) "is-active")
        :on-click #(rf/dispatch [:set-active-tab key])}
   [:a label]])

(defn tabs []
  (let [active-tab (rf/subscribe [:active-tab])]
    (fn []
      [b/tabs
       [:<>
        [tab :sdgs-pie "SDGs" @active-tab]
        [tab :targets-pie "Targets" @active-tab]
        [tab :targets-details "Details" @active-tab]]
       {:alignment "is-centered is-fullwidth" :size :is-large}])))

(defn plot []
  (let [active-tab (rf/subscribe [:active-tab])
        interaction-data (rf/subscribe [:interaction-data])]
    (when-not (seq @interaction-data)
      (rf/dispatch [:request-interaction-data]))
    (fn []
      (case @active-tab
        :sdgs-pie [pie-score-distribution-sdg]
        :targets-pie [pie-score-distribution-target]
        :targets-details [target-details]
        [pie-score-distribution-sdg]))))

(defn app []
  [b/section
   [b/container
    [b/title "Interaction"]
    [tabs]
    [:br]
    [:div {:style {:height 720}}
     [plot]]]])

(comment
  (rf/dispatch [:initialize-db])
  (rf/dispatch [:request-interaction-data])

  (def x @(rf/subscribe [:interaction-data-sdgs-targets true]))

  (r/render [app] (.getElementById js/document "app"))
  (r/render [heatmap-sdgs] (.getElementById js/document "app"))
  @(rf/subscribe
    [:interaction-data-targets-pair->details ["2" "3"]])
  #_(def interaction-data (rf/subscribe [ :interaction-data])))
