package dev.JustRed23.uat.backend;

import static dev.JustRed23.uat.backend.ControllerAddresses.Method.*;

@SuppressWarnings("unused")
public final class ControllerAddresses {

    public static String SITE_NAME = "default";

    public static final Route //API Common Endpoints
            API_LOGIN = new Route("/api/login", POST),
            API_LOGOUT = new Route("/api/logout", POST),
            API_SELF = new Route("/api/self", GET),
            API_SITES = new Route("/api/self/sites", GET),
            API_SITE = new Route("/api/s/" + SITE_NAME, GET),
            SITE_CMD = new Route(API_SITE.getRoute() + "/cmd", POST);

    public static final Route //Managers
            EVTMGT = new Route(SITE_CMD.getRoute() + "/evtmgt", POST),
            SITEMGR = new Route(SITE_CMD.getRoute() + "/sitemgr", POST),
            STAMGR = new Route(SITE_CMD.getRoute() + "/stamgr", POST),
            DEVMGR = new Route(SITE_CMD.getRoute() + "/devmgr", POST);

    public static class Route {

        private final String route;
        private final Method method;

        public Route(String route, Method method) {
            this.route = route;
            this.method = method;
        }

        public String getRoute() {
            return route;
        }

        public String getMethod() {
            return method.name();
        }
    }

    public enum Method {
        GET, POST, PUT
    }
}
