package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private static final String API_KEY = "apikey=edc14c3f";
    private static final String ENDERECO = "https://www.omdbapi.com/?";
    private final Scanner scan = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {


        System.out.print("Digite o nome da série para busca: ");
        var serie = scan.nextLine();

        String endereco = ENDERECO.concat(API_KEY).concat("&t=").concat(serie.replace(" ", "+"));
        System.out.println(endereco);
        var json = consumoAPI.obterDados(endereco);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);

        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumoAPI.obterDados(endereco.concat("&Season=%d".formatted(i)));
            DadosTemporada temporada = conversor.obterDados(json, DadosTemporada.class);

            temporadas.add(temporada);
        }
        temporadas.forEach(System.out::println);
//
//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodios = temporadas.get(i).epsisodios();
//            for (int j = 0; j < episodios.size(); j++) {
//                System.out.println(episodios.get(j).titulo());
//            }
//        }

        temporadas.forEach(temporada -> temporada.epsisodios().forEach(episodio -> System.out.printf("S%dE%d - %s%n", temporada.numero(), episodio.numero(), episodio.titulo())));

        List<DadosEpisodio> dadosEpisodios =
                temporadas.stream().flatMap(temporada -> temporada.epsisodios().stream()).collect(Collectors.toList());

//        System.out.println("Top 10 episódios");
//        dadosEpisodios.stream()
//                .filter(episodio -> !episodio.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(episodio -> System.out.println("Primeiro Filtro (N/A) " + episodio))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(episodio -> System.out.println("Ordenação " + episodio))
//                .limit(10)
//                .peek(episodio -> System.out.println("Limite " + episodio))
//                .map(episodio -> episodio.titulo().toUpperCase())
//                .peek(episodio -> System.out.println("Mapeamento " + episodio))
//                .forEach(System.out::println);


        List<Episodio> episodios =
                temporadas.stream()
                        .flatMap(temporada -> temporada.epsisodios().stream().map(de -> new Episodio(temporada.numero(), de))).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um episódio para buscar?");
        var trechoTitulo = scan.nextLine();

        System.out.println(trechoTitulo);

        Optional<Episodio> episodioBuscado = episodios.stream().filter(episodio -> episodio.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase())).findFirst();

        if (episodioBuscado.isPresent()) {
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada() + " Titulo: " + episodioBuscado.get().getTitulo());
        } else {
            System.out.println("Episódio não encontrado");
        }


//
//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = scan.nextInt();
//        scan.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream().filter(episodio -> episodio.getDataLancamento() != null && episodio.getDataLancamento().isAfter(dataBusca))
//                .forEach(episodio -> System.out.println(
//                        "Temporada: " + episodio.getTemporada() +
//                                " Episodio: " + episodio.getTitulo() +
//                                " Data lançamento: " + episodio.getDataLancamento().format(formatador)
//                ));


//        List<String> nomes = Arrays.asList("Jaque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//
//        nomes.stream()
//                .sorted() // Ordena
//                .limit(3) // Limita os resultados
//                .filter(nome -> nome.startsWith("N")) // Filtra
//                .map(String::toUpperCase) // Transforma
//                .forEach(System.out::println);

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(episodio -> episodio.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream().filter(episodio -> episodio.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Media: " + est.getAverage());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Quantidade episódio: " + est.getCount());

    }
}