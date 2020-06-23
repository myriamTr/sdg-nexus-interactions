(ns interaction.references.views
  (:require [bulma-cljs.core :as b]
            [re-frame.core :as rf]))

(defn reference [m]
  [b/column {:class [:is-half]}
   [:div {:style {:margin "10px 50px"}}
    [:a {:href (m "link") :target "_blank" :style {:padding-right 10}}
     [:span.icon
      [:i {:class "fas fa-external-link-alt" :style {:padding 20}}]]] (m "citation")]])

(defn references-view []
  (let [reference-data (rf/subscribe [:references-data])]
    (when-not (seq @reference-data)
      (rf/dispatch [:request-references-data]))
    (fn []
      [b/columns {:class [:is-multiline :is-centered]}
       (when (seq @reference-data)
         (let [data (->> @reference-data
                         (group-by #(% "citation"))
                         (reduce-kv #(assoc %1 %2 (first %3)) (sorted-map))
                         vals)]
           (doall (for [[i m] (map-indexed vector data)
                        :when (m "link")]
                    ^{:key i} [reference m]))))])))
