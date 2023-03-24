import java.io.IOException;

/*
TODO:
- enable user to determine the game of life's rules
- create killer cell (natural killers? ^^)
- actually it makes more sense for cell to become Jesus,
  the revival thus should not come from LifeBoard
*/

public class GameOfLife {

    public static LifeBoard board = null;
    
    public static void main(String[] args) {
        int[] arr = {3, 2, 1, -1, 0, 0};
        
        UserInterface UI = new UserInterface();
        try {
           UI.run(); 
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
