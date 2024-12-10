# Heat Propagation
## Abstract
Simulating the temperature change in a metal alloy in parallel as it is heated up from top left and bottom right corners. Uses Sockets in Server Implementation in order to run the right partition elsewhere. 

## Background
Consider a rectangular metal alloy, four times as wide as high, consisting of three different metals, each with different thermal characteristics. Simulate how the metal alloy changes in temperature as it is heated from the top left and the bottom right corner. 

Each metal cell is comprised of three metals with independent heat constant values.
The values for S, T, C_1, C_2, C_3, the dimensions of the mesh, and the threshold should be parameters of the program. Note, however, that combinations of these parameters do not do not converge well. Try values of (0.75, 1.0, 1.25) C1, C2, C3 for your test/demo.

Lastly, this product should be implemented with a server connection. 

## Tools/Skills Used
1. Java Concurrency 
2. Server Sockets + Sockets 
2. Java Swing
3. ForkJoinTask 

## Program Results
![Metal Heating with Size 4.](/outputs/size4.gif)

## Run Guide
### Local Machine
1. Run MetalDecomposition

### Server Based
1. Run Server.java 
2. Run MetalDecomposition

## Future Lessons
1. If dealing with relaxation problems, have a structure to store your new values while retrieving data from the original dataset.
2. If you follow step 1, then you can use threads to break apart a problem by rows/columns. 

## Resources:
1. https://gee.cs.oswego.edu/dl/csc375/a3V2.html

