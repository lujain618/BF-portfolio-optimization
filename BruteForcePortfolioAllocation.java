import java.io.BufferedReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;
public class BruteForcePortfolioAllocation {
   static List<Asset> assets; // List to store all the assets
   static List<Asset> optimalAllocation;// Optimal allocation of assets
   static double maxRisk;  // Maximum allowed risk in the portfolio
   static double maxInvestment; // Maximum allowed investment amount
   static double maxReturn; // Maximum expected return
   
   public static void main(String[] args) {
      readInput("Example2.txt");// Read input from the text file
      bruteForce(); // Call the brute force method to find optimal allocation
      writeOutput("Output_BruteForce.txt"); // Write optimal allocation and results to output file
   }
   
   static void bruteForce() {
      Stack<State> stack = new Stack<>();//stack to store all possible allocation(State)
      stack.push(new State(0, 0, new ArrayList<>()));
      while (!stack.isEmpty()) {
         State currentState = stack.pop();
         int currentIndex = currentState.currentIndex;
         double currentRisk = currentState.currentRisk;
         List<Asset> currentAllocation = currentState.currentAllocation;
         if (currentIndex == assets.size()) {/* If all assets are considered
         Calculate the total quantities of the allocation */
            double totalQuantity = currentAllocation.stream().mapToDouble(a -> a.quantity).sum();
            // Check if the current allocation is within the risk and investment constraints
            if (currentRisk <= maxRisk && totalQuantity <= maxInvestment) {
               // Calculate the current expected return of the allocation
               double currentReturn = currentAllocation.stream().mapToDouble(a -> (a.expectedReturn * a.quantity)/1000).sum();
               // If the current allocation has a higher expected return, update the optimal allocation
               if (currentReturn > maxReturn) {
                  maxReturn = currentReturn;
                  optimalAllocation = new ArrayList<>(currentAllocation);
               }
            }
         } else {
            // Get the current asset
            Asset currentAsset = assets.get(currentIndex);
            int currentQuantity = currentAsset.quantity;
            // Try different quantities of the current asset in the portfolio
            for (int i = 0; i <= currentQuantity; i++) {
            //a copy of the current asset being considered with diffrent quantity
               Asset clonedAsset = new Asset(currentAsset.id, currentAsset.expectedReturn, currentAsset.riskLevel, i);
               //a list that represents the current allocation of assets being considered
               List<Asset> clonedAllocation = new ArrayList<>(currentAllocation);
               clonedAllocation.add(clonedAsset);
               double weight = i/maxInvestment;
               stack.push(new State(currentIndex + 1, currentRisk + currentAsset.riskLevel * weight, clonedAllocation));
            }
         }
      }
   }
   //read input from file
   static void readInput(String fileName) {
      long noOfLines = -1;
      try (Stream<String> fileStream = Files.lines(Paths.get(fileName))) { 
         noOfLines = (int) fileStream.count();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
         assets = new ArrayList<>();
         String line;
         long counter = 0;
         while ((line = br.readLine()) != null) {
            if (counter<noOfLines-2) {  
               String[] parts = line.split(":");
               String id = parts[0].trim();
               double expectedReturn = Double.parseDouble(parts[1].trim());
               double riskLevel = Double.parseDouble(parts[2].trim());
               int quantity = Integer.parseInt(parts[3].trim());
               assets.add(new Asset(id, expectedReturn, riskLevel, quantity));
            }
            else { 
               String[] parts2 = line.split("\\s+"); 
               if((parts2[0]).equalsIgnoreCase("Total")) {
                  maxInvestment = Double.parseDouble(parts2[3].trim());
               }
               if((parts2[0]).equalsIgnoreCase("Risk")) {
                  maxRisk = Double.parseDouble(parts2[4].trim());
               }
            }
            counter++;
         }
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
   //write output to file
   static void writeOutput(String fileName) {
      try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
         bw.write("Optimal Allocation:\n");
         for (Asset asset : optimalAllocation) {
            bw.write(asset.id + ": " + asset.quantity + " units\n");
         }
         bw.write("Expected Portfolio Return: " + maxReturn + "\n");
         bw.write("Portfolio Risk Level: " + maxRisk + "\n");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   /*State is an object that holds the current portfolio allocation,
   each state holds a possible allocation for the portfolio*/
   static class State {
      int currentIndex;//index of the current asset in currentAllocation list
      double currentRisk;//total risk of the cuurentAllocation list
      List<Asset> currentAllocation;//a list to store the current allocation 
   
      public State(int currentIndex, double currentRisk, List<Asset> currentAllocation) {
         this.currentIndex = currentIndex;
         this.currentRisk = currentRisk;
         this.currentAllocation = currentAllocation;
      }
   }
}