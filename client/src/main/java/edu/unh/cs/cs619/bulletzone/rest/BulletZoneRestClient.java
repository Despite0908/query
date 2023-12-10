package edu.unh.cs.cs619.bulletzone.rest;

import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService.*;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientHeaders.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.DoubleWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.InventoryWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.PlayerWrapper;
import edu.unh.cs.cs619.bulletzone.util.StringArrayWrapper;

/** "http://stman1.cs.unh.edu:6191/games"
 * "http://10.0.0.145:6191/games"
 * http://10.0.2.2:8080/
 * Created by simon on 10/1/14.
 */

@Rest(rootUrl = "http://stman1.cs.unh.edu:61906/games",
        converters = {StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class}
        // TODO: disable intercepting and logging
        // , interceptors = { HttpLoggerInterceptor.class }
)
public interface BulletZoneRestClient extends RestClientErrorHandling {
    void setRootUrl(String rootUrl);

    @Post("/{id}")
    PlayerWrapper join(@Path int id) throws RestClientException;

    @Get("")
    GridWrapper grid();

//    @Get("/{millis}/event")
//    StringArrayWrapper event(long millis);

    @Put("/account/register/{username}/{password}")
    BooleanWrapper register(@Path String username, @Path String password);

    @Put("/account/login/{username}/{password}")
    LongWrapper login(@Path String username, @Path String password);

    @Get("/account/balance/{id}")
    DoubleWrapper balance(@Path long id);

    @Put("/{tankId}/move/{direction}")
    LongWrapper move(@Path long tankId, @Path byte direction);

    @Put("/{tankId}/turn/{direction}")
    BooleanWrapper turn(@Path long tankId, @Path byte direction);

    @Put("/{tankId}/fire/{bulletType}")
    BooleanWrapper fire(@Path long tankId, @Path int bulletType);

    @Put("/{builderId}/build/{improvementType}")
    BooleanWrapper build(@Path long builderId, @Path byte improvementType);

    @Delete("/{tankId}/leave")
    BooleanWrapper leave(@Path long tankId);

    @Put("/GetInventory/{id}")
    InventoryWrapper getInventory(@Path int id);

    @Put("/{tankId}/eject")
    LongWrapper eject(@Path long tankId);

    @Get("/tank/health/{tankId}")
    Integer getTankHealth(@Path long tankId);

    @Get("/soldier/health/{soldierId}")
    Integer getSoldierHealth(@Path long soldierId);

    @Get("/builder/health/{builderId}")
    Integer getBuilderHealth(@Path long builderId);


}
