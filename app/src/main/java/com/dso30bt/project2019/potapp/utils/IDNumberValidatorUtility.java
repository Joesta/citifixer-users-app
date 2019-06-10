package com.dso30bt.project2019.potapp.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDNumberValidatorUtility {
    /**
     * Private constructor to hide implicit public one.
     */
    private IDNumberValidatorUtility() {

    }

    private static int generateLuhnDigit(String input) {
        int total = 0;
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            int multiple = (count % 2) + 1;
            count++;
            int temp = multiple * Integer.parseInt(String.valueOf(input.charAt(i)));
            temp = (int) Math.floor(temp / 10) + (temp % 10);
            total += temp;
        }

        total = (total * 9) % 10;

        return total;
    }

    private static boolean validate(String idNumber) {
        try {
            Pattern pattern = Pattern.compile("[0-9]{13}");
            Matcher matcher = pattern.matcher(idNumber);

            if (!matcher.matches()) {
                return false;
            }

            if (!validateDate(idNumber.substring(0, 6))) {
                return false;
            }
            int lastNumber = Integer.parseInt(String.valueOf(idNumber.charAt(idNumber.length() - 1)));
            String numberSection = idNumber.substring(0, idNumber.length() - 1);

            return lastNumber == generateLuhnDigit(numberSection);
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean validateDate(String date) {
        int year = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(2, 4));

        if (month < 1 || month > 12) {
            return false;
        }

        int day = Integer.parseInt(date.substring(4, 6));
        Calendar myCal = new GregorianCalendar(year, month, day);
        int maxDays = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (day < 1 || day > maxDays) {
            return false;
        }

        return true;
    }

    private static Date getBirthdate(String idNumber) {
        int year = Integer.parseInt(idNumber.substring(0, 2));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;

        int prefix = 1900;
        if (year < currentYear) {
            prefix = 2000;
        }
        year += prefix;

        int month = Integer.parseInt(idNumber.substring(2, 4));
        int day = Integer.parseInt(idNumber.substring(4, 6));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar.getTime();
    }

    public static IDNumberDetails extractInformation(String idNumber) {
        if (!validate(idNumber)) {
            return new IDNumberDetails(idNumber, false);
        }

        Date birthDate = getBirthdate(idNumber);
        boolean male = Integer.parseInt(idNumber.substring(6, 7)) >= 5;
        boolean citizen = Integer.parseInt(idNumber.substring(10, 11)) == 0;

        return new IDNumberDetails(idNumber, birthDate, male, citizen, true);
    }

    public static class IDNumberDetails {
        private String idNumber;
        private boolean valid;
        private Date birthDate;
        private boolean male;
        private boolean citizen;

        /**
         * @param idNumber
         * @param valid
         */
        public IDNumberDetails(String idNumber, boolean valid) {
            super();
            this.idNumber = idNumber;
            this.valid = valid;
        }

        public IDNumberDetails(String idNumber, Date birthDate, boolean male, boolean citizen, boolean valid) {
            this.idNumber = idNumber;
            this.birthDate = birthDate;
            this.valid = valid;
            this.male = male;
            this.citizen = citizen;
        }

        /**
         * @return the birthDate
         */
        public Date getBirthDate() {
            return birthDate;
        }

        /**
         * @return the male
         */
        public boolean isMale() {
            return male;
        }

        /**
         * @return the citizen
         */
        public boolean isCitizen() {
            return citizen;
        }

        /**
         * @return the idNumber
         */
        public String getIdNumber() {
            return idNumber;
        }

        /**
         * @return the valid
         */
        public boolean isValid() {
            return valid;
        }
    }
}