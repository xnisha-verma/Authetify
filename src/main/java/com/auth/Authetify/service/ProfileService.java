package com.auth.Authetify.service;

import com.auth.Authetify.io.ProfileRequest;
import com.auth.Authetify.io.ProfileResponse;

public interface ProfileService {
   ProfileResponse createProfile(ProfileRequest request);
}
