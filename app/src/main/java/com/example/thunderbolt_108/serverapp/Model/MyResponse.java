package com.example.thunderbolt_108.serverapp.Model;

import java.util.List;

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int cannonical_ids;
    public List<Result> results;
}
