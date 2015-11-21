package com.company;


/**
 * Created by Maxim  Toyberman on 8/20/15.
 */
public class Sbox {

    final int MAX_NUMBER_1_BYTE = 255;


    public int addPolynomials(int first, int second) {
        /**
         * add two polynomials if gf(2) is just to make "^" between the numbers and return the hexadecimal representaion.
         */
        return first ^ second;
    }

    public String dividePolynomialByX(int number) {
        /**
         * divide polynomial by X is just to shift right the number.
         */
        return Integer.toHexString(number >> 1);
    }

    public  String multiplyPolynomialByX(int number) {
        /**
         * The method shifts every bit to the left  (multiply by x) ,if the number takes more then 1 byte (8 bits) make "^" in modulus 0x11B
         */
        int polynom = number << 1;

        return polynom < MAX_NUMBER_1_BYTE ? Integer.toHexString(polynom) : Integer.toHexString((polynom ^ 0x11B));

    }

    public int checkPairity(int number) {
        /**
         * shifting the number to right(divide by 2) and take the "&" with the 0x1 will give the current bit (first bit) 0 or 1
         * if pairity is 1 then number of 1's is odd else it's even
         */
        int temp = number, pairity = 0;
        while (temp > 0) {
            pairity ^= (temp & 0x1);
            temp = temp >> 1;
        }
        return pairity;
    }

    public int scalarMultiplication(int first, int second) {
        /**
         * logical and between 2 numbers and pass it to check pairity
         */
        return checkPairity(first & second);

    }

    public String multiplyPolynomials(int first_polynomial, int second_polynomial) {
        /**
         * @param first_polynomial ,is the first received polynom.
         * @param second_polynomial ,is the second polynom received.
         * this method is the implementation of multiplication of polynomials in field modulo 11B
         */
        int result = 0;
        while (second_polynomial > 0) {
            //second number is odd
            if ((second_polynomial & 0x1) == 1) {
                result ^= first_polynomial;
            }
            //shift left ,multiply by x,in field modulo 11B
            first_polynomial = Integer.parseInt(multiplyPolynomialByX(first_polynomial), 16);
            //divide by x
            second_polynomial = second_polynomial >> 1;
        }
        return Integer.toHexString(result);
    }

    public int polynomialInverse(int polynomial) {
        /**
         * this method finds the inverse of a number (multiplication of polynomials equals 1 )
         */
        if(polynomial==0)
            return 0;

        for (int i = 0; i <= 0xFF; i++) {

            if (Integer.parseInt(multiplyPolynomials(i, polynomial), 16) == 1)
                return i;
        }
        return -1;
    }

    public int matrixByPolynomialMultiplication(int matrix_polynomial, int polynomial) {
        /**
         * this method multiplies a matrix by polynomial ,first calculating the "row" value and calling the scalar multiplicaton
         * and concatenating the result.
         */
        int result_bit = 0, result = 0;

        for (int i = 0; i < 8; i++) {

            //scalar multiplication beetween the matrix and polynomial
            result_bit = scalarMultiplication(matrix_polynomial, polynomial);
            //shifting the result to the left
            result <<= 1;
            //concatinating the new bit to the rֱֱֱesult
            result |= result_bit;
            //circular bitwise right shifting
            matrix_polynomial = ((matrix_polynomial >> 1) | (matrix_polynomial << (7))) & 0xFF;

        }
        return result;
    }

    public int getSboxValue(int number) {

        return matrixByPolynomialMultiplication(0xF8, polynomialInverse(number)) ^ 0x63;

    }

    public int getInverseSboxValue(int number) {
        //Note: addition in gf(2) is similar to substraction

        return polynomialInverse(matrixByPolynomialMultiplication(0x52,addPolynomials(number,0x63)));


    }

    public void printSbox(){

        String value="";
        System.out.println("Sbox:\n");
        for (int i = 0; i <=0xF ; i++) {
            for (int j = 0; j<=0xF ; j++) {
                value=Integer.toHexString(i)+Integer.toHexString(j);
                System.out.print(String.format("%02x ", getSboxValue(Integer.parseInt(value, 16))));
            }
            System.out.println();
        }

    }

    public void printInverseSbox(){

        String value="";
        System.out.println("Inverse Sbox:\n");
        for (int i = 0; i <=0xF ; i++) {
            for (int j = 0; j<=0xF ; j++) {
                value=Integer.toHexString(i)+Integer.toHexString(j);
                System.out.print(String.format("%02x ", getInverseSboxValue(Integer.parseInt(value, 16))));
            }
            System.out.println();
        }

    }


}
