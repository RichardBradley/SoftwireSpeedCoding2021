Reading a JSON file in Java using Jackson Databind to generate data classes
--------------------------------------------------------------------

Assuming you are using IntelliJ

1. Create a new project (File -> New -> Project, then select Maven)

2. (Optional but a good habit!) Make a git repository
   ```
   git init .
   ```

3. Add libraries:
   1. Open the pom.xml file, right click and choose "generate... -> Dependency"
   1. Suggested libraries:
      1. jackson-databind, for JSON to data classes
      2. jackson-module-parameter-names, for binding immutable classes with Jackson
      3. guava, for lots of useful libs including the Preconditions class
      4. lombok, for boilerplate reduction (probably skip this if you're not already familiar with it)
        * If you are using lombok with jackson-databind, looks like you need the setting I've added in lombok.config
      5. commons-math3, often useful in speed coding for least common multiple, prime testing, nCr and so on
      6. com.google.truth, pretty assertions like assertThat(x).isEqualTo(y)

4. Generate typed classes for our example JSON file
   1. Create the data mapping classes. Use an inner class to keep everything in one file, or split it out for ease of navigation:
      ```java
    @Value
    static class MenuRoot {
        List<MenuItem> menu;
    }

    @Value
    static class MenuItem {
        String dishName;
        int cost;
    }
      ```

   4. Let's add code to read in the JSON file and deserialize it into our structure:
      ```java
        String input = Resources.toString(Resources.getResource("genericSample.json"), StandardCharsets.UTF_8);
        MenuRoot menu = objectMapper.readValue(input, MenuRoot.class);
      ```

5. Finally let's do something with the data we've read:
   ```java
        System.out.println("Today's menu:");
        System.out.println("----");
        for (MenuItem menuItem : menu.menu) {
            System.out.printf("%1$-30s £%s\n", menuItem.dishName, menuItem.cost);
        }
   ```
   and run the project by clicking the "run" icon next to the main method
