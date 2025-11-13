# Lesson 1.9: Part 1 Capstone Project

## 🎉 Congratulations!

You've made it to the final lesson of Part 1! You've learned:

✅ What programming is and how computers think
✅ How to use the Kotlin Playground
✅ How to print output with `println()`
✅ How to store data in variables (`val` and `var`)
✅ The different data types (Int, Double, String, Boolean)
✅ How to get user input with `readLine()`
✅ How to create functions without parameters
✅ How to create functions with parameters and return values

Now it's time to **put it all together** in a real, interactive program!

---

## The Project: Personal Profile Generator

You're going to build a **Personal Profile Generator** - a program that:

1. Asks the user for information about themselves
2. Stores that information in variables
3. Processes the data (does calculations, combines text)
4. Displays a nicely formatted profile using functions

---

## Project Requirements

Your program must:

1. **Ask for user input:**
   - Name
   - Age
   - Birth year
   - Favorite hobby
   - Favorite number

2. **Perform calculations:**
   - Calculate how old they'll be in 10 years
   - Double their favorite number

3. **Use functions:**
   - At least one function that takes parameters
   - At least one function that returns a value

4. **Display a formatted profile:**
   - Use functions to organize the output
   - Make it visually appealing with borders/lines

---

## Step-by-Step Guide

Let me walk you through building this step-by-step!

---

### Step 1: Plan Your Functions

Before writing code, let's plan what functions we'll need:

1. **`printLine()`** - Prints a decorative line
2. **`printHeader(title: String)`** - Prints a section header
3. **`calculateFutureAge(currentAge: Int, years: Int): Int`** - Returns future age
4. **`doubleNumber(num: Int): Int`** - Returns a number doubled

---

### Step 2: Start with the Basic Structure

```kotlin
fun main() {
    // We'll put our code here
}
```

---

### Step 3: Create Helper Functions

Let's create the utility functions first:

```kotlin
fun printLine() {
    println("========================================")
}

fun printHeader(title: String) {
    printLine()
    println("  $title")
    printLine()
}

fun calculateFutureAge(currentAge: Int, years: Int): Int {
    return currentAge + years
}

fun doubleNumber(num: Int): Int {
    return num * 2
}
```

---

### Step 4: Get User Input

In `main()`, let's gather information:

```kotlin
fun main() {
    println("Welcome to the Personal Profile Generator!")
    println()

    print("Enter your name: ")
    val name = readLine()

    print("Enter your age: ")
    val age = readLine()!!.toInt()

    print("Enter your birth year: ")
    val birthYear = readLine()!!.toInt()

    print("Enter your favorite hobby: ")
    val hobby = readLine()

    print("Enter your favorite number: ")
    val favoriteNum = readLine()!!.toInt()

    // Next: process and display!
}
```

---

### Step 5: Process the Data

Calculate derived values:

```kotlin
// Inside main(), after getting input:

val futureAge = calculateFutureAge(age, 10)
val doubledNumber = doubleNumber(favoriteNum)
val currentYear = 2024
val calculatedAge = currentYear - birthYear
```

---

### Step 6: Display the Profile

Use your functions to create beautiful output:

```kotlin
// Inside main(), after calculations:

println()
printHeader("YOUR PROFILE")
println()

println("Name: $name")
println("Current Age: $age years old")
println("Birth Year: $birthYear")
println()

println("In 10 years, you will be $futureAge years old!")
println()

println("Favorite Hobby: $hobby")
println("Favorite Number: $favoriteNum")
println("Your favorite number doubled: $doubledNumber")
println()

printLine()
println("Thanks for using the Profile Generator!")
printLine()
```

---

## The Complete Program

Here's the full solution:

```kotlin
fun printLine() {
    println("========================================")
}

fun printHeader(title: String) {
    printLine()
    println("  $title")
    printLine()
}

fun calculateFutureAge(currentAge: Int, years: Int): Int {
    return currentAge + years
}

fun doubleNumber(num: Int): Int {
    return num * 2
}

fun main() {
    println("Welcome to the Personal Profile Generator!")
    println()

    // Get user input
    print("Enter your name: ")
    val name = readLine()

    print("Enter your age: ")
    val age = readLine()!!.toInt()

    print("Enter your birth year: ")
    val birthYear = readLine()!!.toInt()

    print("Enter your favorite hobby: ")
    val hobby = readLine()

    print("Enter your favorite number: ")
    val favoriteNum = readLine()!!.toInt()

    // Process data
    val futureAge = calculateFutureAge(age, 10)
    val doubledNumber = doubleNumber(favoriteNum)

    // Display profile
    println()
    printHeader("YOUR PROFILE")
    println()

    println("Name: $name")
    println("Current Age: $age years old")
    println("Birth Year: $birthYear")
    println()

    println("In 10 years, you will be $futureAge years old!")
    println()

    println("Favorite Hobby: $hobby")
    println("Favorite Number: $favoriteNum")
    println("Your favorite number doubled: $doubledNumber")
    println()

    printLine()
    println("Thanks for using the Profile Generator!")
    printLine()
}
```

---

## Sample Output

```
Welcome to the Personal Profile Generator!

Enter your name: Jordan
Enter your age: 25
Enter your birth year: 1999
Enter your favorite hobby: hiking
Enter your favorite number: 7

========================================
  YOUR PROFILE
========================================

Name: Jordan
Current Age: 25 years old
Birth Year: 1999

In 10 years, you will be 35 years old!

Favorite Hobby: hiking
Favorite Number: 7
Your favorite number doubled: 14

========================================
Thanks for using the Profile Generator!
========================================
```

---

## Your Turn: Build It!

Now it's your turn to build this program from scratch!

**Challenge Levels:**

### Level 1: Follow the Guide
Copy the complete program above into the code editor and run it. Make sure you understand every line.

### Level 2: Customize It
Make these changes:
- Change the decorative lines to use different characters (like `***` or `---`)
- Add one more question (e.g., "favorite color")
- Add one more calculation (e.g., triple their favorite number)

### Level 3: Extend It
Add these features:
- Create a `formatName()` function that takes a name and returns it in uppercase
- Create a `isAdult()` function that returns `true` if age >= 18, `false` otherwise
- Display whether the user is an adult or not in the profile

---

## Extension Ideas (Advanced)

Want to push yourself even further? Try these:

1. **Add a Menu System:**
   - Let the user choose what information to display
   - Create functions for each section

2. **Add Validation:**
   - Check if the age is positive
   - Check if the birth year makes sense

3. **Create Multiple Profiles:**
   - Store information for multiple people using lists (you'll learn this in Part 2!)

---

## What You've Accomplished

By completing this project, you've demonstrated mastery of:

✅ **Variables:** Storing user input
✅ **Data Types:** Working with String, Int
✅ **User Input:** Using `readLine()`
✅ **Functions:** Creating reusable, organized code
✅ **Parameters:** Passing data to functions
✅ **Return Values:** Getting results from functions
✅ **String Templates:** Formatting output
✅ **Program Structure:** Building a complete, real application

---

## Reflection Questions

Before moving on, think about these:

1. How did using functions make your code more organized?
2. What would happen if you needed to change the border style? How many places would you need to update?
3. Can you think of other programs that might follow a similar pattern (input → process → output)?

---

## What's Next?

**You've completed Part 1!** You now have a solid foundation in Kotlin basics.

In **Part 2**, you'll learn:
- How to make your programs make decisions (`if`, `when`)
- How to make your programs repeat tasks (`for`, `while` loops)
- How to work with collections of data (Lists, Maps)

This is where your programs become truly powerful and intelligent!

---

## Final Thoughts

🎉 **Congratulations!** You've gone from knowing nothing about programming to building a complete, interactive Kotlin program!

You should be proud of yourself. Many people start learning to code, but you've taken it seriously and made real progress.

**Remember:**
- Programming is a skill that improves with practice
- Every expert was once a beginner
- The fact that you made it this far shows you have what it takes

Keep going! The journey is just beginning.

---

## Celebrate Your Achievement!

Before moving to Part 2:

1. **Run your program** and see it work
2. **Share it** with someone (show them what you built!)
3. **Mark this lesson complete** - you've earned it!

**Ready for Part 2?** Click "Next" when you're ready to level up!

---

**Key Takeaways:**
- You can build complete programs with what you've learned
- Functions organize code into logical pieces
- Input → Process → Output is a fundamental program pattern
- Real projects combine multiple concepts together
- You're officially a Kotlin programmer now!

---

Incredible work! You've completed Part 1 of the Kotlin Training Course! 🚀
