# immutant-keycloak

Example Ring web app served with [Immutant](http://immutant.org/) and secured with
[Keycloak](http://keycloak.jboss.org/)

## Usage

* You'll need a `keycloak.json` with your client configuration under `resources/` folder
* The example code expects that the client is configured to `localhost:3030`
* `lein run` should be enough to start a web server


## Technical details

The authentication is based on Keycloak's
[Undertow Adapter](https://github.com/keycloak/keycloak/tree/1.9.x/adapters/oidc/undertow)

## License

Distributed under the Apache License 2.0
