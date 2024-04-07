package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") int totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {

    // Serialize = Objeto -> JSON
    // Deserialize = JSON -> Objeto
    //@JsonProperty - Usa o nome para serializar e desserializar o JSON
    //@JsonAlias - usa o nome para desserializar apenas
}