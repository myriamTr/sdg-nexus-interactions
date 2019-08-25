(ns interaction.core
  [:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [interaction.events]
   [interaction.subs]
   [interaction.views :refer (app)]])


(defn main []
  (rf/dispatch [:initialise-db])
  (r/render [app] (.getElementById js/document "app")))

(defn ^:dev/after-load start []
  (r/render [app] (.getElementById js/document "app")))
