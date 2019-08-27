(ns data.core
  (:require
   [reagent.crypt :as rc]
   [geheimnis.aes]
   [goog.crypt :as gcry]
   [goog.crypt.Aes :as aes]
   [clojure.string :as str]
   ))

(defn main [& cli-args]
  (println "Hello"))

(defn ^:dev/after-load start []
  (println "Start"))

;; (def encrypter (goog.crypt.Aes. (clj->js (range 32))))

;; (defn encrypt [s key]
;;   (->> s
;;        rc/string->bytes
;;        (geheimnis.aes/encrypt key)))

;; (defn decrypt [msg key]
;;   (->> msg
;;       (geheimnis.aes/decrypt key)
;;       (mapv #(.fromCharCode js/String %))
;;       (str/join "")))

;; (def fs (js/require "fs"))

;; (def data (.readFileSync fs "public/data/matrix_interactions.csv" "utf-8"))
;; (def data-encrypted (encrypt data "hello-mimi"))
;; (.writeFileSync
;;  fs
;;  "public/data/matrix_interactions_encrypted.txt"
;;  data-encrypted)
