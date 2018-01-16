package steamgiftbot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import static java.nio.file.Files.lines;
import static java.nio.file.Files.readAllLines;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class BotGift {
    String url="https://www.steamgifts.com";
    String session_cookie = "";
    String token="";
    int points;
    int currentPage;
    ArrayList<String> lista_juegos = new ArrayList <>();
    ArrayList<Game> currentGamesOnPage = new ArrayList <>();
    Date date=new Date();    
    
    
    public void downloadFile(String id,String typeOfFile){
        // https://docs.google.com/document/d/[FILE_ID]/export?format=[FORMAT]
        File file = new File("listGames.txt");
        String stringUrl = "https://docs.google.com/document/d/"+id+"/export?format="+typeOfFile;
        try {
            URL url = new URL(stringUrl);
            FileUtils.copyURLToFile(url, file);
        }catch (Exception e){
            System.out.println("Error en la URL o creacion de fichero: "+e.getMessage());
        }
        
    }
    
    public void loadGameListFromFile (String name) {
        
        Path path = Paths.get(name);
        try{
            this.lista_juegos = (ArrayList) readAllLines(path);           
        }catch(IOException e){
            System.out.println("Error al leer el fichero: "+e.getMessage());
        }
       
        
    }
    
    
    public Document nextPage() {
        String urlpage="/giveaways/search?page="+this.currentPage+1;
        String url = this.url + urlpage;
        Document doc=null;
         try{
            Response res = Jsoup.connect(url).execute();
            doc = Jsoup.connect(url).cookie("PHPSESSID",this.session_cookie).get();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return doc;
    }

    public Document getHtml (String url) {
        Document doc = null;
        try{
            Response res = Jsoup.connect(url).execute();
            doc = Jsoup.connect(url).cookie("PHPSESSID",this.session_cookie).get();
            //System.out.println("Cargando datos de "+url);
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return doc;
    }
    
    public void loadGamesOnPage(Document htmlcode){
        this.currentGamesOnPage.clear();
        String name,code;
        int points;
        
        Elements elementos = htmlcode.select(".giveaway__row-inner-wrap").not(".is-faded");
       
        for (Element elemento : elementos){
            name = elemento.select(".giveaway__heading__name").text();
            code = formatCode(elemento.select("h2.giveaway__heading a").first().attr("href"));
            points = formatPointsToInt(elemento.select(".giveaway__heading__thin").last().text());
            Game game = new Game (name,code,points);
            this.currentGamesOnPage.add(game);
        }    
        
    }
    
    public String formatCode(String code){
        String[] parts = code.split("/");
        return  parts[2];
    }
    public int formatPointsToInt(String stringPoints){
        return Integer.parseInt(stringPoints.substring(1, stringPoints.length()-2));
    }
    
    //Load actual points
    public void loadPoints (Document htmlcode) {
       Elements elementos = htmlcode.select(".nav__points");
       this.points = Integer.parseInt(elementos.text());   
    }
    public void loadToken (Document htmlcode){
       Elements elementos = htmlcode.select("input[name=xsrf_token");
       this.token = elementos.attr("value");
    }
    
    
    public void joinGiveaway (String code){
        Document doc = null;
        String url = this.url+"/ajax.php";
        String toDO = "entry_insert";
        String body ="";
        //System.out.println("Peticion: "+url+" / "+code);
        try{
            doc = Jsoup.connect(this.url+"/ajax.php")
                    //.method(Connection.Method.POST)
                    .data("xsrf_token",this.token)
                    .data("do",toDO)
                    .data("code",code)
                    .cookie("PHPSESSID",session_cookie)
                    .userAgent("Mozilla")
                    .post();
         
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }
   public void searchGamesOnCurrentPage () throws InterruptedException{
       boolean find=false;
       int i=0,j =0;
       while (i < currentGamesOnPage.size()){ 
           find = false;
           while(j<lista_juegos.size() && !find && (this.points > currentGamesOnPage.get(i).points)){
               if (currentGamesOnPage.get(i).name.equalsIgnoreCase(lista_juegos.get(j))){
                    Thread.sleep(8000);
                   joinGiveaway(currentGamesOnPage.get(i).code);
                   this.points = this.points - currentGamesOnPage.get(i).points;
                   System.out.println("Apuntado al sorteo: "+currentGamesOnPage.get(i).name+ " en la página: "+this.currentPage);
                   find = true;
               }
               j++;
           }
           j=0;
           i++;
           
            
       }
  
   }
   public void showData(){
       System.out.println(this.url);
       System.out.println(this.session_cookie);
       System.out.println(this.token);
       System.out.println(this.points);
       System.out.println(this.currentPage);
   }
    
    
}
