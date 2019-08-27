(ns interaction.events
  [:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [day8.re-frame.async-flow-fx]
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

(def interaction-files ["b2e670f0-671c-429f-a430-5e682fbf5397"
                        "a4a346f2-066b-4375-816f-a342454bdbcb"
                        "beba899e-979a-4775-aa8e-f69477695a26"
                        "b3280882-b7f8-4e01-af59-01bbd8fec953"
                        "69857036-face-42f9-85f0-9515d384082d"
                        "b7fd1f79-6f3d-4154-b592-06bafeb90962"
                        "79dee18b-a0b0-48ab-8214-ee262a5e826d"
                        "3e704eb2-9de3-4b1a-a160-64d32c91ebca"
                        "972c1ecd-76df-4f2e-8506-3edf5256ac22"
                        "59cd0029-f51b-4226-9ee6-511eafc0a51b"
                        "b43e4e69-43cb-4400-9bf8-14c352793d35"
                        "4cdab391-1126-4735-81f0-92d8a272fae1"
                        "d292d804-7bcb-4836-9103-31e7747d3820"
                        "c4a78590-1976-48fe-acee-987fa4b367a4"
                        "1adc32e9-918c-40ca-a9dc-5ad7c84d5ec7"
                        "a39c9f3c-f5d2-476a-816b-6d00f440f5db"
                        "fad0501a-8578-4847-b6db-51a86dd19f56"
                        "ab0e966b-d4f8-41ce-becb-83149cec9166"
                        "95a4f144-7074-45f2-9858-ff0bb6c6f74f"
                        "3a7f0cbe-09a9-4ff1-a44e-de64e31f4309"
                        "5daf450c-5bbb-4406-90ef-4dd6a332c42a"
                        "da7bcd60-c3ea-41bd-9092-24184e381711"
                        "d3c6c16d-c114-4dab-8f66-96942e305405"
                        "2fe7abf7-51c2-46ef-883a-3bb73063d33c"
                        "cecd1345-3dbf-47a9-97d8-4ed3d6e82276"
                        "febc5a38-8666-4c11-a06e-ce144baedca5"
                        "808b2544-86d3-4101-b0b1-d5af66caecf6"
                        "06756685-d5f3-47c1-b2ff-229ac9356a6d"
                        "56ec200d-ca40-4e72-8256-28f160847716"
                        "0085bc99-96db-4aa1-bb3f-6d1ee44c54fd"
                        "c3ddbcd5-7c0d-4d2f-9714-0fdcf2c272c4"
                        "3b4163fc-db93-44c6-a401-6a5edfdadb1e"
                        "bcaa60da-ed30-4aba-b0b3-0d52e51413ad"
                        "cce7b235-e920-432f-89db-a7547a97eb48"
                        "fcfb7d69-0787-4896-881e-7b88b8d59b94"
                        "221a85e8-f8a4-4e10-b37a-46a1b7573926"
                        "c6b53d25-3b16-4f7c-a8e3-f4a9be637267"
                        "ccb0c617-69a7-42f6-8f8b-47c05bb2e673"
                        "28a3e0d1-6690-46f7-81f8-830fd302d9ce"
                        "a75aa825-4d88-4992-9924-421b4af6303d"
                        "b5ef6464-4c96-432b-be1a-38a1c8031898"
                        "0c5e6bf1-87d0-4ab9-8b70-dde7af02d6b7"
                        "0f8fadb6-de1d-492f-85e8-2068c2d81a08"
                        "6a74d9f0-b5d6-46e2-9cf1-709b07962d11"
                        "4aae4609-6f8e-4b5d-ba1d-896729ae0b3e"
                        "b1a9c8b0-b78f-459e-be82-7b0c4a8dc109"
                        "0fdc1428-b9b6-4642-8394-e819c2aa0080"
                        "4dbfd4d6-79d7-488e-9cc8-4141fb5d5d77"
                        "fcd6c431-eb6f-481b-a717-ea752757f731"
                        "b37d24fd-6b17-4203-b992-a82fa733fb0e"])

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
