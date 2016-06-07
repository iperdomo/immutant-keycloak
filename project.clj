(defproject immutant-keycloak "0.0.1-SNAPSHOT"
  :description "Immutant Web - Keycloak integration"
  :url ""
  :license {:name "Apache License 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.immutant/web "2.1.4"]
                 [io.undertow/undertow-core "1.3.15.Final"]
                 [org.keycloak/keycloak-undertow-adapter "1.9.7.Final"]
                 [compojure "1.5.0"]]
  :main immutant-keycloak.core)
