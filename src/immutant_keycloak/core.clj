(ns immutant-keycloak.core
  (:require [clojure.java.io :as io]
            [immutant.web :as web]
            [immutant.web.internal.undertow :as undertow])
  (:import [io.undertow.security.api AuthenticationMode]
           [io.undertow.security.handlers AuthenticationCallHandler
            AuthenticationConstraintHandler AuthenticationMechanismsHandler
            SecurityInitialHandler]
           [io.undertow.security.idm IdentityManager Account Credential]
           [io.undertow.security.impl CachedAuthenticatedSessionMechanism]
           [io.undertow.server.session InMemorySessionManager]
           [org.keycloak.adapters KeycloakDeploymentBuilder AdapterDeploymentContext
            NodesRegistrationManagement]
           [org.keycloak.adapters.undertow UndertowUserSessionManagement
            UndertowAuthenticationMechanism UndertowPreAuthActionsHandler
            UndertowAuthenticatedActionsHandler])
  (:gen-class))

(def server-opts {:host "localhost" :port 3030 :path "/"})

(def idm (reify IdentityManager
           (^Account verify [_ ^Account account]
            account)
           (^Account verify [_ ^String id ^Credential credential]
            (throw (IllegalStateException. "Should never be called in Keycloak flow")))
           (^Account verify [_ ^Credential credential]
            (throw (IllegalStateException. "Should never be called in Keycloak flow")))))

(defn wrap-security [handler]
  (let [session-manager (InMemorySessionManager. "SESSION_MANAGER")
        deployment (KeycloakDeploymentBuilder/build (io/input-stream (io/resource "keycloak.json")))
        deployment-context (AdapterDeploymentContext. deployment)
        session-management (UndertowUserSessionManagement.)
        nodes-management (NodesRegistrationManagement.)
        auth-mechanisms [(CachedAuthenticatedSessionMechanism.)
                         (UndertowAuthenticationMechanism. deployment-context
                                                           session-management
                                                           nodes-management
                                                           -1
                                                           nil)]
        ]
    (.registerSessionListener session-manager session-management)
    (-> (undertow/create-http-handler handler)
        (AuthenticationCallHandler.)
        (AuthenticationConstraintHandler.)
        (AuthenticationMechanismsHandler. auth-mechanisms)
        (->> (UndertowAuthenticatedActionsHandler. deployment-context)
             (UndertowPreAuthActionsHandler. deployment-context session-management session-manager)
             (SecurityInitialHandler. AuthenticationMode/PRO_ACTIVE idm)))))

(defn app
  [req]
  {:status 200
   :body (-> req
             :server-exchange
             (.getSecurityContext)
             (.getAuthenticatedAccount)
             (.getPrincipal)
             (.getName))})

(defn -main []
  (web/run (wrap-security app) server-opts))
