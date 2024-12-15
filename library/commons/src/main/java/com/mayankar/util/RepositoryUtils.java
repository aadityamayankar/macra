package com.mayankar.util;

import com.mayankar.model.BaseEntityProfile;
import org.springframework.stereotype.Component;

import static com.mayankar.util.Constants.MISC_FLAG_DELETED;

@Component
public class RepositoryUtils {
    public <T extends BaseEntityProfile> boolean isDeleted(T entity) {
        return entity.getMiscflags() != null && (entity.getMiscflags() & MISC_FLAG_DELETED) == MISC_FLAG_DELETED;
    }
}
