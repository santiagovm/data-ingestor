package com.vasquezhouse.batch.earthquakes.ingestion.worker.domain;

import java.util.HashMap;
import java.util.Map;

public class UsaStatesMap {

    public static String findUsaState(String value) {
        return STATES.getOrDefault(value.toUpperCase(), null);
    }

    public static boolean isUsaState(String value) {
        return STATES.containsKey(value.toUpperCase());
    }

    private static final Map<String, String> STATES = new HashMap<>() {{
        put("AL", "AL"); put("ALA", "AL"); put("ALABAMA", "AL");
        put("AK", "AK"); put("ALK", "AK"); put("ALASKA", "AK");
        put("AZ", "AZ"); put("ARIZ", "AZ"); put("ARIZONA", "AZ");
        put("AR", "AR"); put("ARK", "AR"); put("ARKANSAS", "AR");
        put("CA", "CA"); put("CAL", "CA"); put("CALIF", "CA"); put("CALIFORNIA", "CA");
        put("CO", "CO"); put("COL", "CO"); put("COLO", "CO"); put("COLORADO", "CO");
        put("CT", "CT"); put("CONN", "CT"); put("CONNECTICUT", "CT");
        put("DE", "DE"); put("DEL", "DE"); put("DELAWARE", "DE");
        put("FL", "FL"); put("FLA", "FL"); put("FLORIDA", "FL");
        put("GA", "GA"); put("GEORGIA", "GA");
        put("HI", "HI"); put("HAW", "HI"); put("HAWAII", "HI");
        put("ID", "ID"); put("IDA", "ID"); put("IDAHO", "ID");
        put("IL", "IL"); put("ILL", "IL"); put("ILLINOIS", "IL");
        put("IN", "IN"); put("IND", "IN"); put("INDIANA", "IN");
        put("IA", "IA"); put("IOWA", "IA");
        put("KS", "KS"); put("KAN", "KS"); put("KANSAS", "KS");
        put("KY", "KY"); put("KEN", "KY"); put("KENTUCKY", "KY");
        put("LA", "LA"); put("LOUISIANA", "LA");
        put("ME", "ME"); put("MAINE", "ME");
        put("MD", "MD"); put("MARYLAND", "MD");
        put("MA", "MA"); put("MASS", "MA"); put("MASSACHUSETTS", "MA");
        put("MI", "MI"); put("MICH", "MI"); put("MICHIGAN", "MI");
        put("MN", "MN"); put("MINN", "MN"); put("MINNESOTA", "MN");
        put("MS", "MS"); put("MISS", "MS"); put("MISSISSIPPI", "MS");
        put("MO", "MO"); put("MISSOURI", "MO");
        put("MT", "MT"); put("MONT", "MT"); put("MONTANA", "MT");
        put("NE", "NE"); put("NEB", "NE"); put("NEBRASKA", "NE");
        put("NV", "NV"); put("NEV", "NV"); put("NEVADA", "NV");
        put("NH", "NH"); put("NEWHAMPSHIRE", "NH"); put("NEW HAMPSHIRE", "NH");
        put("NJ", "NJ"); put("NEWJERSEY", "NJ"); put("NEW JERSEY", "NJ");
        put("NM", "NM"); put("NEWMEXICO", "NM"); put("NEW MEXICO", "NM");
        put("NY", "NY"); put("NEWYORK", "NY"); put("NEW YORK", "NY");
        put("NC", "NC"); put("NORTHCAROLINA", "NC"); put("NORTH CAROLINA", "NC");
        put("ND", "ND"); put("NORTHDAKOTA", "ND"); put("NORTH DAKOTA", "ND");
        put("OH", "OH"); put("OHIO", "OH");
        put("OK", "OK"); put("OKLA", "OK"); put("OKLAHOMA", "OK");
        put("OR", "OR"); put("ORE", "OR"); put("OREGON", "OR");
        put("PA", "PA"); put("PENN", "PA"); put("PENNSYLVANIA", "PA");
        put("RI", "RI"); put("RHODEISLAND", "RI"); put("RHODE ISLAND", "RI");
        put("SC", "SC"); put("SOUTHCAROLINA", "SC"); put("SOUTH CAROLINA", "SC");
        put("SD", "SD"); put("SOUTHDAKOTA", "SD"); put("SOUTH DAKOTA", "SD");
        put("TN", "TN"); put("TENN", "TN"); put("TENNESSEE", "TN");
        put("TX", "TX"); put("TEX", "TX"); put("TEXAS", "TX");
        put("UT", "UT"); put("UTAH", "UT");
        put("VT", "VT"); put("VERMONT", "VT");
        put("VA", "VA"); put("VIRGINIA", "VA");
        put("WA", "WA"); put("WASH", "WA"); put("WASHINGTON", "WA");
        put("WV", "WV"); put("WESTVIRGINIA", "WV"); put("WEST VIRGINIA", "WV");
        put("WI", "WI"); put("WIS", "WI"); put("WISC", "WI"); put("WISCONSIN", "WI");
        put("WY", "WY"); put("WYO", "WY"); put("WYOMING", "WY");
    }};
}
