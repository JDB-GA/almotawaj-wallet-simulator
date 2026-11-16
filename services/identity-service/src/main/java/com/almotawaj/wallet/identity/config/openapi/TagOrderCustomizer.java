package com.almotawaj.wallet.identity.config.openapi;

import com.almotawaj.wallet.identity.config.constants.docs.AdminStatisticsDocs;
import com.almotawaj.wallet.identity.config.constants.docs.AuthDocs;
import com.almotawaj.wallet.identity.config.constants.docs.KycDocs;
import com.almotawaj.wallet.identity.config.constants.docs.KycReviewDocs;
import com.almotawaj.wallet.identity.config.constants.docs.ProfileDocs;
import com.almotawaj.wallet.identity.config.constants.docs.SystemDocs;
import com.almotawaj.wallet.identity.config.constants.docs.UserAdminDocs;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TagOrderCustomizer implements OpenApiCustomizer {
    private static final List<String> ORDER = List.of(AuthDocs.TAG, ProfileDocs.TAG, KycDocs.TAG, KycReviewDocs.TAG, UserAdminDocs.TAG,
            UserAdminDocs.PERMISSIONS_TAG, AdminStatisticsDocs.TAG, SystemDocs.SEED_TAG);

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getTags() != null) {
            openApi.getTags().sort(Comparator.comparingInt((Tag tag) -> rank(tag.getName())));
        }
    }

    private static int rank(String name) {
        int index = ORDER.indexOf(name);
        return index < 0 ? ORDER.size() : index;
    }
}
