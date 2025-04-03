(ns clojure-demo.core
  (:gen-class)
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css]]
            [clojure.string :as str])
  (:import [org.eclipse.jetty.server Server]
           [org.eclipse.jetty.server.handler AbstractHandler]
           [java.io InputStreamReader BufferedReader]
           [java.net URLDecoder]))

;; Utility to parse form data
(defn parse-form-data [request]
  (when-let [body (.getInputStream request)]
    (let [reader (BufferedReader. (InputStreamReader. body "UTF-8"))
          form-string (.readLine reader)]
      (when form-string
        (->> (str/split form-string #"&")
             (map #(str/split % #"="))
             (map (fn [[k v]] [(URLDecoder/decode k "UTF-8")
                              (URLDecoder/decode v "UTF-8")]))
             (into {}))))))

;; Prime number utilities
(defn prime? [n]
  (if (< n 2)
    false
    (not-any? #(zero? (mod n %)) (range 2 (inc (Math/sqrt n))))))

(defn primes []
  (filter prime? (iterate inc 2)))

;; HTML components
(defn layout [content]
  (html5
   [:head
    [:title "Clojure Prime Explorer"]
    (include-css "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css")]
   [:body
    [:div.container.mt-5
     [:h1.text-center "Clojure Prime Explorer"]
     content]]))

(defn home-page []
  (layout
   [:div
    [:div.row.justify-content-center
     [:div.col-md-6
      [:div.card
       [:div.card-body
        [:h5.card-title "Check if a number is prime"]
        [:form {:action "/check" :method "POST"}
         [:div.mb-3
          [:input.form-control {:type "text" 
                               :name "number" 
                               :placeholder "Enter a number"
                               :required true}]]
         [:button.btn.btn-primary {:type "submit"} "Check"]]]]]]
    [:div.row.mt-4
     [:div.col-12
      [:h3 "First 50 Prime Numbers"]
      [:div {:style "max-height: 300px; overflow-y: auto;"}
       [:table.table.table-striped
        [:tbody
         (doall
          (for [p (take 50 (primes))]
            [:tr [:td p]]))]]]]]]))

(defn result-page [number is-prime]
  (layout
   [:div.row.justify-content-center
    [:div.col-md-6
     [:div.card
      [:div.card-body
       [:h5.card-title (str "Is " number " prime?")]
       [:p.card-text
        (if is-prime
          [:span.text-success "Yes, it is prime!"]
          [:span.text-danger "No, it is not prime."])]
       [:a.btn.btn-primary {:href "/"} "Check another number"]]]]]))

;; Jetty Handler
(defn make-handler []
  (proxy [AbstractHandler] []
    (handle [target base-request request response]
      (let [method (.getMethod request)
            uri (.getRequestURI request)]
        
        (.setContentType response "text/html; charset=utf-8")
        
        (case [method uri]
          ["GET" "/"]
          (do
            (.setStatus response 200)
            (.write (.getWriter response) (home-page)))
          
          ["POST" "/check"]
          (let [params (parse-form-data request)
                number-str (get params "number")]
            (.setStatus response 200)
            (try
              (if (and number-str (not-empty number-str))
                (let [n (Integer/parseInt number-str)]
                  (.write (.getWriter response) 
                         (result-page n (prime? n))))
                (.write (.getWriter response) 
                       (layout [:div.alert.alert-danger "Please enter a number"])))
              (catch NumberFormatException _
                (.write (.getWriter response)
                       (layout [:div.alert.alert-danger "Please enter a valid number"])))))
          
          ;; Default 404 response
          (do
            (.setStatus response 404)
            (.write (.getWriter response) "Page not found")))
        
        (.setHandled base-request true)))))

;; Server setup
(defn -main [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))
        server (Server. port)]
    (.setHandler server (make-handler))
    (.start server)
    (.join server))) 