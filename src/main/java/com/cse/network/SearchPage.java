package com.cse.network;

import com.cse.module.DocSearchModule;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by leeyh on 2016. 11. 7..
 */
@Path("/search")
public class SearchPage {

    @GET
    public Response search(@QueryParam("q")String query){
        return Response.ok(new Gson().toJson(DocSearchModule.getInstance().search(query))).header("Access-Control-Allow-Origin","*").build();
    }
}
