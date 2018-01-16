package steamgiftbot;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;

public class SteamgiftBot {

    public static void main(String[] args){
        
        long min = 1800000;
      
      TimerTask timerTask = new TimerTask() { 
         public void run(){ 
             try {
                 
                 iniciar();
             } catch (InterruptedException ex) {
                 Logger.getLogger(SteamgiftBot.class.getName()).log(Level.SEVERE, null, ex);
             }
         } 
     }; 
      Timer timer = new Timer(); 
      timer.scheduleAtFixedRate(timerTask, 0, min);
       
    }
    public static void  iniciar() throws InterruptedException{
        Date date = new Date();
        System.out.println("Realización del escaneo: " + new SimpleDateFormat("dd.MM.yyyy  HH:mm").format(date));
        String url = "https://www.steamgifts.com/";
        Document htmlcode;
        BotGift bot = new BotGift();
         
        bot.downloadFile("", "txt");
        bot.loadGameListFromFile("listGames.txt");
        
        htmlcode = bot.getHtml(url);
        bot.loadPoints (htmlcode);
        bot.loadToken(htmlcode);
        
        bot.currentPage=1;
        
        bot.loadGamesOnPage(htmlcode);
        bot.searchGamesOnCurrentPage();
        
        while (bot.points > 10 && bot.currentPage < 20){
            bot.currentPage = bot.currentPage + 1;
            htmlcode = bot.getHtml("https://www.steamgifts.com/"+"giveaways/search?page="+bot.currentPage);
            bot.loadGamesOnPage(htmlcode);
            bot.searchGamesOnCurrentPage();
        }
    }
    
    
}
