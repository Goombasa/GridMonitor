package edu.boisestate.matthewchristens.gridmonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;

public class GridMonitor implements GridMonitorInterface {

    private double[][] baseGrid;
    private double[][] surroundingSumGrid;
    private double[][] surroundingAvgGrid;
    private double[][] deltaGrid;
    private boolean[][] dangerGrid;

    /**
     * Main constructor. Fills in all grids with proper information
     *
     * @param filename the name of the file containing grid values
     * @throws FileNotFoundException thrown if the file does not exist
     */
    public GridMonitor(String filename) throws FileNotFoundException {
        // if the file name is not given, throw FileNotFoundException
        if (filename == null || filename.isEmpty()) throw new FileNotFoundException();

        //Construct a file object from the local directory and the filename variable
        //TODO: try to find file from only given path, then use local directory as backup
        File inputFile = new File(Paths.get("").toAbsolutePath().toString(), filename);

        //initialize dimension variables
        int dimX = -1, dimY = -1;

        //initialize scanner
        Scanner inputScanner = new Scanner(inputFile);
        //while there is more content in the file
        while (inputScanner.hasNext()) {
            //get the next item in the file
            String next = inputScanner.next();

            //if dimY has not been properly set
            if (dimY == -1)
                //try to set dimY
                try {
                    dimY = Integer.parseInt(next);
                } catch (NumberFormatException err) {
                    //print the stack trace and return if the passed string is not a number
                    err.printStackTrace();
                    return;
                }
            //if dimY is set but dimX is not
            else if (dimX == -1)
                //try to set dimX, then break the while-loop
                try {
                    dimX = Integer.parseInt(next);
                    break;
                } catch (NumberFormatException err) {
                    //print the stack trace and return if the passed string is not a number
                    err.printStackTrace();
                    return;
                }
        }

        //initialize each of the grids using the dimensions we just set
        baseGrid = new double[dimY][dimX];
        surroundingSumGrid = new double[dimY][dimX];
        surroundingAvgGrid = new double[dimY][dimX];
        deltaGrid = new double[dimY][dimX];
        dangerGrid = new boolean[dimY][dimX];

        //loop through each y value
        yloop: for (int y = 0; y < dimY; y++) {
            //loop through each x value
            for (int x = 0; x < dimX; x++) {
                //break the outer loop if there are no more values to add
                if (!inputScanner.hasNext()) break yloop;

                //try to set the baseGrid[y][x] value to the next value
                try {
                    baseGrid[y][x] = Double.parseDouble(inputScanner.next());
                } catch (NumberFormatException err) {
                    //print the stack trace and return if the passed string is not a number
                    err.printStackTrace();
                    return;
                }
            }
        }

        //close the scanner as it is no longer needed
        inputScanner.close();

        //loop through all of the y values again
        for (int y = 0; y < dimY; y++) {
            //loop through all of the x values agian
            for (int x = 0; x < dimX; x++) {
                //create some variables to store the values of the adjacent cells
                double above, below, left, right, sum;

                //if the cell is at the top, set top to the cell's value
                if (y == 0) above = baseGrid[y][x];
                //otherwise, set top to the value of the cell above
                else above = baseGrid[y-1][x];

                //if the cell is at the bottom, set bottom to the cell's value
                if (y == dimY-1) below = baseGrid[y][x];
                //otherwise, set bottom to the value of the cell bellow
                else below = baseGrid[y+1][x];

                //if the cell is on the left edge, set left to the cell's value
                if (x == 0) left = baseGrid[y][x];
                //otherwise, set left to the value of the cell to the left
                else left = baseGrid[y][x-1];

                //if the cell is on the right edge, set right to the cell's value
                if (x == dimX-1) right = baseGrid[y][x];
                //otherwise, set right to the value of the cell to the right
                else right = baseGrid[y][x+1];

                //sum all the values of the surrounding cells
                sum = above + below + left + right;

                //set surroundingSumGrid[y][x] to the value of the surrounding sum for this cell
                surroundingSumGrid[y][x] = sum;

                //set surroundingAvgGrid[y][x] to the average value of the surrounding cells
                surroundingAvgGrid[y][x] = sum/4;

                //find the deltas for this cell and save them to deltaGrid[y][x]
                deltaGrid[y][x] = Math.abs(sum/8);

                //determine if the cell is in danger and save the truth value to dangerGrid[y][x]
                dangerGrid[y][x] = baseGrid[y][x] < surroundingAvgGrid[y][x] - deltaGrid[y][x] || baseGrid[y][x] > surroundingAvgGrid[y][x] + deltaGrid[y][x];
            }
        }

    }

    @Override
    public double[][] getBaseGrid() {
        //return a copy of baseGrid
        return copy2DArray(baseGrid);
    }

    @Override
    public double[][] getSurroundingSumGrid() {
        //return a copy of surroundingSumGrid
        return copy2DArray(surroundingSumGrid);
    }

    @Override
    public double[][] getSurroundingAvgGrid() {
        //return a copy of surroundingAvgGrid
        return copy2DArray(surroundingAvgGrid);
    }

    @Override
    public double[][] getDeltaGrid() {
        //return a copy of deltaGrid
        return copy2DArray(deltaGrid);
    }

    @Override
    public boolean[][] getDangerGrid() {
        //return a copy of dangerGrid
        return copy2DArray(dangerGrid);
    }

    /**
     * A function that creates a copy of the passed 2D array
     *
     * @param toCopy the array to create a copy of
     * @return the copied array
     */
    private double[][] copy2DArray(double[][] toCopy) {
        //initialize a new array of the same dimensions
        double[][] toReturn = new double[toCopy.length][toCopy[0].length];

        //loop through every row
        for (int y = 0; y < toCopy.length; y++) {
            //use System.arraycopy to copy the row to the new array at the same position
            System.arraycopy(toCopy[y], 0, toReturn[y], 0, toCopy[y].length);
        }

        //return the copied array
        return toReturn;
    }

    /**
     * A function that creates a copy of the passed 2D array
     *
     * @param toCopy the array to create a copy of
     * @return the copied array
     */
    private boolean[][] copy2DArray(boolean[][] toCopy) {
        //initialize a new array of the same dimensions
        boolean[][] toReturn = new boolean[toCopy.length][toCopy[0].length];

        //loop through every row
        for (int y = 0; y < toCopy.length; y++) {
            //use System.arraycopy to copy the row to the new array at the same position
            System.arraycopy(toCopy[y], 0, toReturn[y], 0, toCopy[y].length);
        }

        //return the copied array
        return toReturn;
    }
}
