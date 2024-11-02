package com.mayankar.dto;

import com.mayankar.model.RoleProfile;
import com.mayankar.model.UserProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileWithDetails extends UserProfile {
    private RoleProfile role;
}
