# Example for StreamNative Platform

```
helm install  -f values_cluster.yaml mycluster streamnative/sn-pulsar  --set initialize=true
```

Wait until all pods are `Running` or `Completed`!

```
kubectl get po -n pulsar

NAME                                               READY   STATUS      RESTARTS   AGE
mycluster-pulsar-bookie-0                          1/1     Running     0          2m28s
mycluster-pulsar-bookie-1                          1/1     Running     0          2m28s
mycluster-pulsar-bookie-2                          1/1     Running     0          2m28s
mycluster-pulsar-broker-0                          1/1     Running     0          2m13s
mycluster-pulsar-proxy-0                           1/1     Running     0          3m32s
mycluster-pulsar-pulsar-init-nqm2z                 0/1     Completed   0          3m33s
mycluster-pulsar-recovery-0                        1/1     Running     0          2m26s
mycluster-pulsar-streamnative-console-0            1/1     Running     0          3m33s
mycluster-pulsar-streamnative-console-init-nhhgc   0/1     Completed   0          3m33s
mycluster-pulsar-toolset-0                         1/1     Running     0          3m33s
mycluster-pulsar-vault-0                           3/3     Running     0          3m32s
mycluster-pulsar-vault-1                           3/3     Running     0          3m12s
mycluster-pulsar-vault-2                           3/3     Running     0          3m2s
mycluster-pulsar-vault-init-tgvbn                  0/1     Completed   0          3m33s
mycluster-pulsar-zookeeper-0                       1/1     Running     0          3m32s
mycluster-pulsar-zookeeper-1                       1/1     Running     0          3m32s
mycluster-pulsar-zookeeper-2                       1/1     Running     0          3m32s
```

To upgrade cluster after changing the cluster configurations, run the following command.

```
helm upgrade -f values_cluster.yaml mycluster streamnative/sn-pulsar
```