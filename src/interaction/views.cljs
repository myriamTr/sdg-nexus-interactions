(ns interaction.views
  (:require
   [interaction.db :refer [sdgs->targets]]
   [interaction.charts.pie :refer [pie-target pie]]
   [interaction.descriptions.views :refer (descriptions-view)]
   [interaction.references.views :refer (references-view)]
   [cljs.pprint :as pprint]
   [clojure.string :as str]
   [reagent.core :as r]
   [reagent.debug :as rd]
   [re-frame.core :as rf]
   [bulma-cljs.core :as b]
   ["react-plotly.js" :default react-plotly]))


;; ## UI components for navigation
;;
;; The intended UX is: SDGs tabs -> Target tabs -> Details (cycle).

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
        [tab :targets-details "Interaction Details" @active-tab]
        [tab :references "References" @active-tab]]
       {:alignment "is-centered is-fullwidth" :size :is-large}])))

(defn input-field [label input-props]
  [:div.field
   [:label.label label]
   [b/input input-props]])

(defn select-sdgs-for-targets [sdg-from sdg-to]
  (let [s-from (r/atom sdg-from)
        s-to (r/atom sdg-to)]
    (fn [sdg-from sdg-to]
      [b/columns
       [b/column {:class :is-half}
        [input-field "From SDG"
         {:value @s-from
          :placeholder "From SDG"
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
    (fn [] [select-targets @target-from @target-to])))

;; ## Plots
;; Mainly wrappers around the interaction.charts.pie namespace to display the value

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

(defn pie-score-distribution-sdg []
  (let [data (rf/subscribe [:interaction-data-sdgs-pie-sum])
        data-loaded? (rf/subscribe [:interaction-data-loaded?])]
    (fn []
      (if @data-loaded? [:<> [pie @data :positive]] [:div]))))

(defn pie-score-distribution-target []
  (let [data (rf/subscribe [:interaction-data-sdgs-targets])
        sdg-from (rf/subscribe [:sdg-from])
        sdg-to (rf/subscribe [:sdg-to])
        data-loaded? (rf/subscribe [:interaction-data-loaded?])]
    (fn []
      (let [sdg-link [@sdg-from @sdg-to]]
        [:<>
         [select-sdgs-for-targets-container]
         [:div {:style {:text-align :center}}
          (if (and @data-loaded? @sdg-from @sdg-to)
            [pie-target (get @data (mapv str sdg-link)) sdg-link]
            [:div])]]))))

;; # Target card interaction details
;;
;; Implements cards to display additional data about the interaction found in
;; the previous tabs

(defn target-card-detail-modal [content]
  (let [activate (r/atom false)
        toggle #(swap! activate not)
        close #(reset! activate false)]
    (fn [content]
      [:<>
       [:button.button.is-text
        {:on-click toggle #_(fn [] (toggle))}
        [:i.fas.fa-bookmark]]
       [:div.modal {:class (when @activate "is-active")}
        [:div.modal-background
         {:style {:background-color "rgba(0, 0, 0, 0.5)"}
          :on-click toggle}]
        [:div.modal-content
         [b/card {:style {:border-radius 2}}
          [b/card-content content]
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
  (let [open? (r/atom false)]
    (fn [m]
      (let [reference-data @(rf/subscribe [:references-data-map])
            neutral-color (fn [a] (str "rgba(150,150,150," a " )"))
            positive-color (fn [a] (str "rgba(51,123,174, " a ")"))
            negative-color (fn [a] (str "rgba(253,60,60," a " )"))
            title (get m "Title")
            card-color (cond (zero? (:score m)) neutral-color
                             (pos? (:score m)) positive-color
                             :else negative-color)
            citation-chicago (get-in reference-data [title "citation"])
            title-corrected
            (let [s (get-in reference-data [title "title_corrected"])]
              (rd/log s)
              (if (seq s) s title))
            card-style {:style {:display :flex
                                :justify-content :space-between
                                :flex-direction :column
                                :height "100%"
                                :border-width 2
                                :border-radius 5
                                :border-style :solid
                                :border-color (card-color 1)
                                :background-color (card-color 0.2)}}
            geographical-place (m "Geographical place")
            key-facts-figures (m "Key facts and figures")
            interaction-comment (m "Describe the tradeoffs / Co-benefit of the interaction")
            governance-role (m "Notes on the role of governance / policy (if mentioned) and the social context")
            lessons-learned
            (m "Describe measures taken to mitigate trade-offs or maximise co-benefits; what are the outcomes, experiences and lessons learnt")
            material (get m "Further material:")]

        [b/column {:class :is-half}
         [b/card card-style
          [:div {:style {:flex "1 0 auto"}}
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
             [:b (:target-from m)
              [:i.fas.fa-arrow-right {:style {:padding-left 10 :padding-right 10}}]
              (:target-to m)]
             [:br]
             [:b "ICSU Score " (m "ICSU scale assessment")]]

            ;; geographical-place
            (when-not (empty? geographical-place)
              [:div {:style {:padding-top 5 :display :flex :align-items :center}}
               [:i.fas.fa-globe.fa-2x {:style {:padding-right 20}}]
               [:b geographical-place]])

            ;; key insight
            [:div {:style {:padding-top 20}}
             [:i (get m "Key insight")]]

            (when-not (empty? material)
              [:<> [:br]
               [:div [:b "Further material: "] material]])

            (when @open?
              (when (seq interaction-comment)
                [:<> [:br]
                 [:div [:b "Additional information: "] interaction-comment]])

              (when (seq key-facts-figures)
                [:<> [:br]
                 [:div [:b "Facts & figures: "] key-facts-figures]])

              #_(when governance-role
                  [:<> [:br]
                   [:div [:b "Role of governance: "] governance-role]])

              #_(when (seq lessons-learned)
                  [:<> [:br]
                   [:div [:b "Lessons learned: "] lessons-learned]]))]]

          [:footer {:class "card-footer"}
           (when (or (seq interaction-comment) (seq key-facts-figures))
             [:a {:class "card-footer-item" :on-click #(swap! open? not)}
              (if @open?
                [:span {:class "icon"}
                 [:i {:class "fas fa-angle-up", :aria-hidden "true"}]]
                [:span {:class "icon"}
                 [:i {:class "fas fa-angle-down", :aria-hidden "true"}]])])]]]))))

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

(defn main-view []
  (let [active-tab (rf/subscribe [:active-tab])]
    (when-not @active-tab
      (rf/dispatch [:set-active-tab :sdgs-pie]))
    (fn []
      (case @active-tab
        :sdgs-pie [pie-score-distribution-sdg]
        :targets-pie [pie-score-distribution-target]
        :targets-details [target-details]
        :references [references-view]
        [pie-score-distribution-sdg]))))

(defn app []
  [:div {:style {:min-height "100vh" :overflow-x :scroll}}
   [b/section
    [b/container
     [tabs]
     [:br]
     [:div {:style {:min-height "720px"}}
      [main-view]]]]])

(comment
  (rf/dispatch [:initialize-db])
  (rf/dispatch [:request-interaction-data])
  #_(rf/dispatch [:request-sdg-metadata])
  (def x @(rf/subscribe [:interaction-data-sdgs-targets true]))

  (r/render [app] (.getElementById js/document "app"))
  (r/render [heatmap-sdgs] (.getElementById js/document "app"))
  @(rf/subscribe
    [:interaction-data-targets-pair->details ["2" "3"]])
  #_(def interaction-data (rf/subscribe [ :interaction-data])))
