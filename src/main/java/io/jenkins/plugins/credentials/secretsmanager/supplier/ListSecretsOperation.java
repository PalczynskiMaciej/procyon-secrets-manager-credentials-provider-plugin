package io.jenkins.plugins.credentials.secretsmanager.supplier;

import com.amazonaws.services.secretsmanager.model.Filter;
import io.jenkins.plugins.credentials.secretsmanager.factory.ProcyonSecretsManager;
import io.jenkins.plugins.credentials.secretsmanager.model.ListSecretsRequest;
import io.jenkins.plugins.credentials.secretsmanager.model.ListSecretsResult;
import io.jenkins.plugins.credentials.secretsmanager.model.SecretListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Look up all secrets in Secrets Manager using the ListSecrets command. Paginate through secrets
 * until there are none left to get.
 */
class ListSecretsOperation implements Supplier<Collection<SecretListEntry>> {

    private final ProcyonSecretsManager client;

    private final Collection<Filter> filters;

    ListSecretsOperation(ProcyonSecretsManager client, Collection<Filter> filters) {
        this.client = client;
        this.filters = filters;
    }

    @Override
    public Collection<SecretListEntry> get() {
        final List<SecretListEntry> secretList = new ArrayList<>();

        Optional<String> nextToken = Optional.empty();
        do {
            final ListSecretsRequest base = new ListSecretsRequest();
            final ListSecretsRequest request = nextToken.map((nt) -> base.withNextToken(nt)).orElse(base);
            try {
                final ListSecretsResult result = client.listSecrets(request);
                final List<SecretListEntry> secrets = result.getSecretList()
                        .stream()
                        .filter(ListSecretsOperation::isNotDeleted)
                        .collect(Collectors.toList());
                secretList.addAll(secrets);
                nextToken = Optional.ofNullable(result.getNextToken());
            } catch (InterruptedException e) {
                return secretList;
            }
        } while (nextToken.isPresent());

        secretList.add(new SecretListEntry());
        return secretList;
    }

    private static boolean isNotDeleted(SecretListEntry entry) {
        return entry.getDeletedDate() == null;
    }
}
