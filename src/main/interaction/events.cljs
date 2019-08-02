(ns interaction.events
  [:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [interaction.db :refer [default-db]]
   [re-frame.core :as rf :refer (reg-event-db reg-event-fx)]
   ["d3" :as d3]])

(reg-event-db
 :initialise-db
 (fn [db _]
   default-db))

(reg-event-db
 :set-active-tab
 (fn [db [_ active-tab]]
   (assoc db :active-tab active-tab)))

(reg-event-db
 :choose-sdgs-polarity
 (fn [db [_ value]]
   (assoc db :heatmap-sdgs-polarity value)))

(reg-event-db
 :choose-targets-polarity
 (fn [db [_ value]]
   (assoc db :heatmap-targets-polarity value)))

(reg-event-db
 :select-sdg-from
 (fn [db [_ sdg-from]]
   (let [x (js/parseInt sdg-from)]
     (if-not (js/isNaN x)
       (assoc db :sdg-from x)
       db))))

(reg-event-db
 :select-sdg-to
 (fn [db [_ sdg-to]]
   (let [x (js/parseInt sdg-to)]
     (if-not (js/isNaN x)
       (assoc db :sdg-to x)
       db))))

(reg-event-db
 :select-target
 (fn [db [_ id target]]
   (assoc-in db [:target id] target)))

(reg-event-fx
 :request-interaction-data
 (fn [{db :db} _]
   {:db db
    :http-xhrio {:method :get
                 :uri "data/matrix_interactions.csv"
                 :format (ajax/json-request-format)
                 :response-format (ajax/text-response-format)
                 :on-success [:request-interaction-data-success]
                 :on-failure [:api-request-error]}}))

(reg-event-fx
 :request-interaction-data-success
 (fn [{db :db} [_ result]]
   {:db (assoc-in db [:data :interaction] (js->clj (.csvParse d3 result)))}))

(reg-event-fx
 :api-request-error
 (fn [{:keys [db]} [_ response]]
 {:db (update-in db [:errors]
                 (fnil conj []) response)}))
