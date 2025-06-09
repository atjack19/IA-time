public class Main {
    public static void main(String[] args) {
        /*
        ArrayList<String> recipes = FileHandler.recipeFileRead();
        for (String recipe : recipes) {
            System.out.println(recipe);
        }

         */

//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("Enter the name of the recipe:");
//        String name = scanner.nextLine();
//
//        System.out.println("Enter the book name:");
//        String book = scanner.nextLine();
//
//        System.out.println("Enter the page number:");
//        int page = Integer.parseInt(scanner.nextLine());
//
//        System.out.println("Enter ingredients separated by commas:");
//        Ingredient[] ingredients = Arrays.stream(scanner.nextLine().split(","))
//                .map(String::trim)
//                .map(Ingredient::new)
//                .toArray(Ingredient[]::new);
//
//        System.out.println("Enter calories:");
//        int calories = Integer.parseInt(scanner.nextLine());
//
//        System.out.println("Enter protein content:");
//        int protein = Integer.parseInt(scanner.nextLine());
//
//        System.out.println("Enter carbs content:");
//        int carbs = Integer.parseInt(scanner.nextLine());
//
//        System.out.println("Enter sugar content:");
//        int sugars = Integer.parseInt(scanner.nextLine());
//
//        System.out.println("Enter fat content:");
//        int fats = Integer.parseInt(scanner.nextLine());
//
//        System.out.println("Enter tags separated by commas:");
//        String[] tags = scanner.nextLine().split(",");
//
//        //Recipe newRecipe = new Recipe(name, book, page, ingredients, calories, protein, carbs, sugars, fats, tags);
//        FileHandler.addRecipe(name, book, page, ingredients, calories, protein, carbs, sugars, fats, tags);
//
//        System.out.println("New recipe added successfully!");
//
//        System.out.println(FileHandler.recipeFileRead());

        //String[] data = {"house","car","wall","dog","apple","soup","School","Alphabetical","Zoo"};

        //BinarySearchTree bstString = new BinarySearchTree();



        //for (String datum : data) {
            //System.out.println("adding " + data[i]);
            //bstString.add(datum);
        //}

        //bstString.display();
        //bstString.reverseDisplay();

        //int[] data2 = {17,37,19,1,4,15};

        //BinarySearchTree bst2 = new BinarySearchTree();
        //for (int datum : data2) {
            //bst2.add(datum);
        //}

        //bst2.display();
        //bst2.reverseDisplay();

        //GUI myGUI = new GUI();

        BinarySearchTree nameBST = FileHandler.createBST(0);
        nameBST.display();
        nameBST.searchFor("name", "A");
    }
}