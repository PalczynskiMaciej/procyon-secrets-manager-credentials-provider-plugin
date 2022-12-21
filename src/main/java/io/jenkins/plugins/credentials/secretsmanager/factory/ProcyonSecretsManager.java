package io.jenkins.plugins.credentials.secretsmanager.factory;

import com.ai.procyon.jenkins.grpc.agent.ListSecretsResponse;
import com.ai.procyon.jenkins.grpc.agent.ListSecretsRequest;
import com.ai.procyon.jenkins.grpc.agent.GetSecretResponse;

public interface ProcyonSecretsManager {

    GetSecretResponse getSecretValue(Integer ID);

    ListSecretsResponse listSecrets(ListSecretsRequest listSecretsRequest) throws InterruptedException;
}
