(ns interaction.core
  [:require
   [reagent.dom :as dom]
   [re-frame.core :as rf]
   [interaction.events]
   [interaction.subs]
   [interaction.views :refer (app)]])

(defn main []
  (rf/dispatch [:initialise-db])
  (rf/dispatch-sync [:boot-request-load])
  (dom/render [app] (.getElementById js/document "app")))

(defn ^:dev/after-load start []
  (rf/clear-subscription-cache!)
  (dom/render [app] (.getElementById js/document "app")))
