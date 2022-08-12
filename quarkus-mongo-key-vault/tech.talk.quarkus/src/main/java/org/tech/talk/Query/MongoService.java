package org.tech.talk.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.UnwindOptions;
import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;

import static com.mongodb.client.model.Aggregates.*;

@ApplicationScoped
public class MongoService {

    @Inject
    MongoClient mongoClient;

    private static final Logger LOG = Logger.getLogger(MongoService.class);

    public ArrayList<Document> getDataAirBnb(){

        return  mongoClient.getDatabase("sample_airbnb").getCollection("listingsAndReviews").aggregate(queryAirBnb())
                .allowDiskUse(true)
                .into(new ArrayList<>());
    }

    private List<Bson> queryAirBnb(){

        List<Bson> query = new ArrayList<>();
        query.add(group(new Document("airbnb", "$_id"),
                push("airbnb","$$ROOT") ));

        query.add(new Document("$unwind","$airbnb"));
        query.add(unwind("$airbnb", new UnwindOptions().preserveNullAndEmptyArrays(true)));

        query.add(match(lte("airbnb.beds", 10)));
        query.add(match(gt("airbnb.bedrooms", 3)));

        return  query;
    }

    public ArrayList<Document> getDatamflix() throws ParseException {
        return  mongoClient.getDatabase("sample_mflix").getCollection("comments")
                .aggregate(queryAgregationMflix())
                .allowDiskUse(true)
                .into(new ArrayList<>());
    }


    private List<Bson> queryAgregationMflix() throws ParseException {

      /*  List<Bson> pipeline = Arrays.asList(
                new Document()
                        .append("$lookup", new Document()
                                .append("from", "users")
                                .append("localField", "email")
                                .append("foreignField", "email")
                                .append("as", "infoUsuario")
                        ),
                new Document()
                        .append("$lookup", new Document()
                                .append("from", "movies")
                                .append("localField", "movie_id")
                                .append("foreignField", "_id")
                                .append("as", "infoMovies")
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$infoUsuario")
                                .append("preserveNullAndEmptyArrays", true)
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$infoMovies")
                                .append("preserveNullAndEmptyArrays", true)
                        ),
                new Document()
                        .append("$project", new Document()
                                .append("_id", 0.0)
                                .append("name", 1.0)
                                .append("email", 1.0)
                                .append("text_movie", "$text")
                                .append("date_movie", "$date")
                                .append("usuario_name", "$infoUsuario.name")
                                .append("movie.title", "$infoMovies.title")
                                .append("movie.genres", "$infoMovies.genres")
                                .append("movie.cast", "$infoMovies.cast")
                                .append("movie.lastupdated", "$infoMovies.lastupdated")
                                .append("poster", "$infoMovies.poster")
                                .append("languajes", "$infoMovies.languages")
                                .append("directors", "$infoMovies.directors")
                                .append("writers", "$infoMovies.writers")
                                .append("year", "$infoMovies.year")
                                .append("countries", "$infoMovies.countries")
                                .append("rating", "$infoMovies.tomatoes.viewer.raiting")
                                .append("numReviews", "$infoMovies.tomatoes.viewer.numReviews")
                                .append("infoCompleta", "$$ROOT")
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("year", new Document()
                                        .append("$gte", 1000.0)
                                )
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("$or", Arrays.asList(
                                                new Document()
                                                        .append("countries", new BsonRegularExpression(".*USA.*")),
                                                new Document()
                                                        .append("countries", new BsonRegularExpression(".*Germany.*"))
                                        )
                                )
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("writers", new Document()
                                        .append("$size", 1.0)
                                )
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("directors", new Document()
                                        .append("$size", 1.0)
                                )
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("languajes", new Document()
                                        .append("$size", 2.0)
                                )
                        ),
                new Document()
                        .append("$project", new Document()
                                .append("name", 1.0)
                                .append("email", 1.0)
                                .append("text_movie", 1.0)
                                .append("date_movie", 1.0)
                                .append("usuario_name", 1.0)
                                .append("movie.title", 1.0)
                                .append("movie.genres", 1.0)
                                .append("movie.cast", 1.0)
                                .append("movie.lastupdated", 1.0)
                                .append("poster", 1.0)
                                .append("languajes", 1.0)
                                .append("directors", 1.0)
                                .append("writers", 1.0)
                                .append("year", 1.0)
                                .append("countries", 1.0)
                                .append("rating", 1.0)
                                .append("numReviews", 1.0)
                                .append("date", "$$NOW")
                                .append("month", new Document()
                                        .append("$dateToString", new Document()
                                                .append("format", "%m")
                                                .append("date", "$date_movie")
                                        )
                                )
                                .append("date_Formater", new Document()
                                        .append("$dateToString", new Document()
                                                .append("format", "%Y-%m-%d")
                                                .append("date", "$date_movie")
                                        )
                                )
                        ),
                new Document()
                        .append("$match", new Document()
                                .append("date", new Document()
                                        .append("$gte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse("1999-12-31 19:00:00.000-0500"))
                                )
                        ),
                new Document()
                        .append("$sort", new Document()
                                .append("date_movie", 1.0)
                        ),
                new Document()
                        .append("$lookup", new Document()
                                .append("from", "users")
                                .append("localField", "email")
                                .append("foreignField", "email")
                                .append("as", "logueo")
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$logueo")
                                .append("preserveNullAndEmptyArrays", true)
                        )
        );*/

        return null;
    }

}
