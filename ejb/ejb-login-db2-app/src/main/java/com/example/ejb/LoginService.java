package com.example.ejb;

import javax.ejb.Remote;

@Remote
public interface LoginService {
  boolean authenticate(String username, String password);
}
