
package steamgiftbot;

public class Game {
    String name;
    String code;
    int points;
    
    public Game (String name, String code, int points){
        this.name = name;
        this.code=code;
        this.points = points;
    }
    public String getName (){
        return this.name;
    }
    public String getCode (){
        return this.code;
    }
    public int getPoints (){
        return this.points;
    }
}
