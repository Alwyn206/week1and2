import java.util.*;

public class FlashSaleInventoryManager {

    // productId -> stock count
    private HashMap<String, Integer> stockMap = new HashMap<>();

    // productId -> waiting list of users
    private HashMap<String, LinkedList<Integer>> waitingList = new HashMap<>();

    // Add product with stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        }

        // Add to waiting list
        LinkedList<Integer> queue = waitingList.get(productId);
        queue.add(userId);

        return "Added to waiting list, position #" + queue.size();
    }

    // Show waiting list
    public List<Integer> getWaitingList(String productId) {
        return waitingList.getOrDefault(productId, new LinkedList<>());
    }

    // Test program
    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 11111));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 88888));

        System.out.println("Waiting List: " + manager.getWaitingList("IPHONE15_256GB"));
    }
}