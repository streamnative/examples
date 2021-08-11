# Example for StreamNative Platform

```
kubectl create namespace <KUBERNETES_NAMESPACE>
export NAMESPACE = <KUBERNETES_NAMESPACE>
helm install  -f values_cluster.yaml <RELEASE_NAME> streamnative/sn-pulsar  --set initialize=true -n $NAMESPACE
```

Wait until all pods are `Running` or `Completed`!

```
kubectl get po -n $NAMESPACE

NAME                                               READY   STATUS      RESTARTS   AGE
RELEASE_NAME-pulsar-bookie-0                          1/1     Running     0          2m28s
RELEASE_NAME-pulsar-bookie-1                          1/1     Running     0          2m28s
RELEASE_NAME-pulsar-bookie-2                          1/1     Running     0          2m28s
RELEASE_NAME-pulsar-broker-0                          1/1     Running     0          2m13s
RELEASE_NAME-pulsar-proxy-0                           1/1     Running     0          3m32s
RELEASE_NAME-pulsar-pulsar-init-nqm2z                 0/1     Completed   0          3m33s
RELEASE_NAME-pulsar-recovery-0                        1/1     Running     0          2m26s
RELEASE_NAME-pulsar-streamnative-console-0            1/1     Running     0          3m33s
RELEASE_NAME-pulsar-streamnative-console-init-nhhgc   0/1     Completed   0          3m33s
RELEASE_NAME-pulsar-toolset-0                         1/1     Running     0          3m33s
RELEASE_NAME-pulsar-vault-0                           3/3     Running     0          3m32s
RELEASE_NAME-pulsar-vault-1                           3/3     Running     0          3m12s
RELEASE_NAME-pulsar-vault-2                           3/3     Running     0          3m2s
RELEASE_NAME-pulsar-vault-init-tgvbn                  0/1     Completed   0          3m33s
RELEASE_NAME-pulsar-zookeeper-0                       1/1     Running     0          3m32s
RELEASE_NAME-pulsar-zookeeper-1                       1/1     Running     0          3m32s
RELEASE_NAME-pulsar-zookeeper-2                       1/1     Running     0          3m32s
```

To upgrade cluster after changing the cluster configurations, run the following command.

```
helm upgrade -f values_cluster.yaml <RELEASE_NAME> streamnative/sn-pulsar -n $NAMESPACE
```