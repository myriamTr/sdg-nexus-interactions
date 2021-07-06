(ns interaction.events
  (:require
   ["d3" :as d3]
   [ajax.core :as ajax]
   [day8.re-frame.async-flow-fx]
   [day8.re-frame.http-fx]
   [interaction.db :refer [default-db]]
   [re-frame.core :as rf :refer (reg-event-db reg-event-fx)]))

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

(def interaction-files
  ["0332ca96-37ad-4e14-a925-b876708455cd" "88568137-59e5-4eff-88f9-f89a4ad2e310"])

(defn loading-data-flow []
  (let [success-events
        (mapv #(conj [:interaction-data-success] %)
              interaction-files)]
    {:first-dispatch [:request-interaction-data-all]
     :rules
     [{:when :seen-all-of? :events success-events
       :dispatch [:set-loaded? :interaction-data]}]}))

(reg-event-fx
 :boot-request-load
 (fn [_ _]
   {:db {:loaded? {:interaction-data false}} :async-flow (loading-data-flow)}))

(reg-event-fx
 :set-loaded?
 (fn [{db :db} [_ k]] {:db (assoc-in db [:loaded? k] true)}))

(reg-event-fx
 :request-interaction-data-all
 (fn [{db :db} _]
   (let [events (mapv #(conj [:request-interaction-data] %) interaction-files)]
     {:db db :dispatch-n events})))

;; Event to notify to async-flow that the event has been sucessful.
(reg-event-fx
 :interaction-data-success
 (fn [{db :db} [_ k]] {:db db}))

(reg-event-fx
 :request-interaction-data
 (fn [{db :db} [_ filename]]
   {:db db
    :http-xhrio {:method :get
                 :uri (str "data/" filename)
                 :format (ajax/json-request-format)
                 :response-format (ajax/text-response-format)
                 :on-success [:request-interaction-data-success filename]
                 :on-failure [:api-request-error]}}))

(reg-event-fx
 :request-interaction-data-success
 (fn [{db :db} [_ k result]]
   (.log js/console "seen success:" k)
   {:db (update-in db [:data :interaction] (fnil into [])
                   (js->clj (.csvParse d3 result)))
    :dispatch [:interaction-data-success k]}))


(reg-event-fx
 :request-references-data
 (fn [{db :db} _]
   {:db db
    :http-xhrio {:method :get
                 :uri "data/references.csv"
                 :format (ajax/json-request-format)
                 :response-format (ajax/text-response-format)
                 :on-success [:request-references-data-success]
                 :on-failure [:api-request-error]}}))

(reg-event-fx
 :request-references-data-success
 (fn [{db :db} [_ result]]
   {:db (assoc-in db [:data :references]
                  (js->clj (.csvParse d3 result)))}))

(reg-event-fx
 :request-sdg-metadata
 (fn [{db :db} _]
   {:db db
    :http-xhrio {:method :get
                 :uri "data/sdg_id_title.json"
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format)
                 :on-success [:request-sdg-metadata-success]
                 :on-failure [:api-request-error]}}))

(reg-event-fx
 :request-sdg-metadata-success
 (fn [{db :db} [_ result]]
   {:db (assoc-in db [:data :sdg-metadata] result)}))

(reg-event-fx
 :api-request-error
 (fn [{:keys [db]} [_ response]]
 {:db (update-in db [:errors]
                 (fnil conj []) response)}))
