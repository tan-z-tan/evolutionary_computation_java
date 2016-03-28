## Evolutionary Computation

### Sample usage (Wallfollowing problem)

Some classes depend on other libraries (Apache commons math library).
Go to website [commons math](https://commons.apache.org/proper/commons-math/download_math.cgi), and download the jar file.

Make sure to specify library path to compile.

e.g.

```
javac -cp commons-math3-3.0.jar:. application/wallFollowing/ProblemDefinition.java
(You may need to compile other related classes. e.g. javac -cp commons-math3-3.0.jar:. geometry/LineShape.java)
```

#### run

```
java application/wallFollowing/ProblemDefinition
```

![wallfollowing image](https://github.com/tan-z-tan/evolutionary_computation_java/blob/master/sample.png)
