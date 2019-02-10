package com.hexagonkt.http.server

import com.hexagonkt.http.client.Client
import com.hexagonkt.helpers.Logger

@Suppress("unused", "MemberVisibilityCanBePrivate") // Test methods are flagged as unused
internal class SessionModule : TestModule() {
    private val log: Logger = Logger(this)

    override fun initialize(): Router = Router {
        get("/session/id") {
            val id: String = session.id ?: "null"
            assert(id == session.id ?: "null")
            ok(id)
        }

        get("/session/access") { ok(session.lastAccessedTime?.toString() ?: "null") }

        get("/session/new") {
            try {
                ok(session.isNew())
            }
            catch(e: Exception) {
                halt("Session error")
            }
        }

        get("/session/inactive") {
            val inactiveInterval = session.maxInactiveInterval ?: "null"
            session.maxInactiveInterval = 999
            assert(inactiveInterval == session.maxInactiveInterval ?: "null")
            ok(inactiveInterval)
        }

        get("/session/creation") { ok(session.creationTime ?: "null") }

        post("/session/invalidate") { session.invalidate() }

        put("/session/{key}/{value}") {
            session.setAttribute(pathParameters["key"], pathParameters["value"])
        }

        get("/session/{key}") {
            ok (session.getAttribute(pathParameters["key"]).toString())
        }

        delete("/session/{key}") {
            session.removeAttribute(pathParameters["key"])
        }

        get("/session") {
            val attributeTexts = session.attributes.entries.map { it.key + " : " + it.value }

            response.setHeader ("attributes", attributeTexts.joinToString(", "))
            response.setHeader ("attribute values", session.attributes.values.joinToString(", "))
            response.setHeader ("attribute names", session.attributes.keys.joinToString(", "))

            response.setHeader ("creation", session.creationTime.toString())
            response.setHeader ("id", session.id ?: "")
            response.setHeader ("last access", session.lastAccessedTime.toString())

            response.status = 200
        }
    }

    fun attribute(client: Client) {
        assert(client.put("/session/foo/bar").statusCode == 200)
        assertResponseEquals(client.get("/session/foo"), "bar")
    }

    fun sessionLifecycle(client: Client) {
        client.post("/session/invalidate")

        assert(client.get("/session/id").responseBody == "null")
        assert(client.get("/session/inactive").responseBody == "null")
        assert(client.get("/session/creation").responseBody == "null")
        assert(client.get("/session/access").responseBody == "null")
        assert(client.get("/session/new").responseBody == "true")

        assert(client.put("/session/foo/bar").statusCode == 200)
        assert(client.put("/session/foo/bazz").statusCode == 200)
        assert(client.put("/session/temporal/_").statusCode == 200)
        assert(client.delete("/session/temporal").statusCode == 200)

        assert(client.get("/session").statusCode == 200)
        assertResponseEquals(client.get("/session/foo"), "bazz")

        assert(client.get("/session/id").responseBody != "null")
        assert(client.get("/session/inactive").responseBody != "null")
        assert(client.get("/session/creation").responseBody != "null")
        assert(client.get("/session/access").responseBody != "null")
        assert(client.get("/session/new").responseBody == "false")

        client.post("/session/invalidate")

        assert(client.get("/session/id").responseBody == "null")
        assert(client.get("/session/inactive").responseBody == "null")
        assert(client.get("/session/creation").responseBody == "null")
        assert(client.get("/session/access").responseBody == "null")
    }

    override fun validate(client: Client) {
        client.cookies.clear()
        attribute(client)
        client.cookies.clear()
        sessionLifecycle(client)
    }
}
