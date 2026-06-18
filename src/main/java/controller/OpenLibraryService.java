package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Endpoints utilizados:
 *  - https://openlibrary.org/search.json?q=...      -> busca geral (titulo/autor/palavra-chave)
 *  - https://openlibrary.org/search.json?isbn=...   -> busca por ISBN
 *  - https://covers.openlibrary.org/b/isbn/{isbn}-M.jpg -> capa do livro
 */
public class OpenLibraryService {

    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/isbn/%s-M.jpg";

    /*Representa dados da API.*/
    public static class LivroResultado {
        public String titulo;
        public String autor;
        public String isbn;
        public String anoPublicacao;
        public String capaUrl;

        @Override
        public String toString() {
            return titulo + (autor != null && !autor.isEmpty() ? " - " + autor : "")
                    + (anoPublicacao != null && !anoPublicacao.isEmpty() ? " (" + anoPublicacao + ")" : "");
        }
    }

    public List<LivroResultado> buscarPorTermo(String termo) throws Exception {
        String query = URLEncoder.encode(termo, StandardCharsets.UTF_8.toString());
        String urlStr = SEARCH_URL + "?q=" + query + "&limit=15"
                + "&fields=title,author_name,isbn,first_publish_year";
        String json = chamarApi(urlStr);
        return parsearDocs(json);
    }

    public List<LivroResultado> buscarPorIsbn(String isbn) throws Exception {
        String isbnLimpo = isbn.replaceAll("[^0-9Xx]", "");
        String urlStr = SEARCH_URL + "?isbn=" + URLEncoder.encode(isbnLimpo, StandardCharsets.UTF_8.toString())
                + "&fields=title,author_name,isbn,first_publish_year";
        String json = chamarApi(urlStr);
        return parsearDocs(json);
    }

    public static String urlCapaPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return null;
        String isbnLimpo = isbn.replaceAll("[^0-9Xx]", "");
        return String.format(COVER_URL, isbnLimpo);
    }

    private String chamarApi(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "controller.BibliotecaApp/1.0 (Java Swing)");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        int code = conn.getResponseCode();
        if (code != 200) {
            throw new Exception("HTTP: " + code);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                sb.append(linha);
            }
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    /**
     * Extrai a lista de "docs" do JSON retornado por search.json e converte
     * cada um em um LivroResultado. Implementacao tolerante e simples,
     * feita sem dependencias externas (somente java.util.regex).*/
    private List<LivroResultado> parsearDocs(String json) {
        List<LivroResultado> resultados = new ArrayList<>();
        if (json == null || json.isEmpty()) return resultados;

        // Localiza o array "docs": [ ... ]
        int docsIdx = json.indexOf("\"docs\"");
        if (docsIdx == -1) return resultados;
        int arrStart = json.indexOf('[', docsIdx);
        if (arrStart == -1) return resultados;

        int depth = 0;
        int objStart = -1;
        for (int i = arrStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) objStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart != -1) {
                    String objJson = json.substring(objStart, i + 1);
                    LivroResultado lr = parsearObjeto(objJson);
                    if (lr != null && lr.titulo != null && !lr.titulo.isEmpty()) {
                        resultados.add(lr);
                    }
                    objStart = -1;
                }
            } else if (c == ']' && depth == 0) {
                break;
            }
        }
        return resultados;
    }

    private LivroResultado parsearObjeto(String obj) {
        LivroResultado lr = new LivroResultado();
        lr.titulo = extrairString(obj, "title");

        String autores = extrairArray(obj, "author_name");
        if (autores != null && !autores.isEmpty()) {
            lr.autor = autores;
        } else {
            lr.autor = "";
        }

        String ano = extrairNumero(obj, "first_publish_year");
        lr.anoPublicacao = ano != null ? ano : "";

        String isbn = extrairPrimeiroIsbn(obj);
        lr.isbn = isbn != null ? isbn : "";

        lr.capaUrl = (lr.isbn != null && !lr.isbn.isEmpty()) ? urlCapaPorIsbn(lr.isbn) : null;
        return lr;
    }

    /*Extrai um valor string simples: "campo": "valor"*/
    private String extrairString(String obj, String campo) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher m = p.matcher(obj);
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        return null;
    }

    /*Extrai um valor numerico: "campo": 1234*/
    private String extrairNumero(String obj, String campo) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(obj);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**Extrai um array de strings e concatena os elementos com ','.
     * Ex: "author_name": ["J. K. Rowling", "Outro"]*/
    private String extrairArray(String obj, String campo) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher m = p.matcher(obj);
        if (!m.find()) return null;

        String conteudo = m.group(1);
        Pattern itemP = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher itemM = itemP.matcher(conteudo);
        List<String> itens = new ArrayList<>();
        while (itemM.find()) {
            itens.add(unescapeJson(itemM.group(1)));
        }
        return String.join(", ", itens);
    }

    private String extrairPrimeiroIsbn(String obj) {
        Pattern p = Pattern.compile("\"isbn\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher m = p.matcher(obj);
        if (!m.find()) return null;

        String conteudo = m.group(1);
        Pattern itemP = Pattern.compile("\"([0-9Xx-]{8,17})\"");
        Matcher itemM = itemP.matcher(conteudo);
        if (itemM.find()) {
            return itemM.group(1);
        }
        return null;
    }

    private String unescapeJson(String s) {
        if (s == null) return null;
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\n", " ")
                .replace("\\t", " ")
                .replace("\\r", " ");
    }
}
