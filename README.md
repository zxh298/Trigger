# Trigger
Find conditional dependency patterns for latent variable discovery.
We call such conditional dependency patterns "trigger" which means they cannot be satisfied by any possible observed 
structures (DAGs) in terms of D-Separation rules. 

How to run:
As it doesnt require any third-party libraries, so just download and copy all java files into your IDE and compile (with
at least Java version 7). The entrance is called "MainClass.java".

The results are shown as below:

     variable number      structures number                   structure number                 number of triggers
                          (without disconnected variables)    (with disconnected variables)                                 
          3                          4                                  6                               0
          4                          24                                 31                              2
          5                          268                                302                             57
		 

"Disconnected" means nodes are totally disconnected in a DAG. For example A is a separated variables in 
the network (A,B,C,D): A B->C<-D


If search with disconnected nodes with 5 observed variables, there are only two more triggers discovered:

   &nbsp; &nbsp; 0 0 0 0 1 1 </br>
   &nbsp; &nbsp; 0 0 0 0 0 0 </br>
   &nbsp; &nbsp; 0 0 0 0 0 1 </br>
   &nbsp; &nbsp; 0 0 0 0 1 0 </br>
   &nbsp; &nbsp; 0 0 0 0 0 0 </br>
   &nbsp; &nbsp; 0 0 0 0 0 0 </br>

***************************************

   &nbsp; &nbsp; 0 0 0 0 1 1 </br>
   &nbsp; &nbsp; 0 0 0 0 0 0 </br>
   &nbsp; &nbsp; 0 0 0 1 0 1 </br>
   &nbsp; &nbsp; 0 0 0 0 1 0 </br>
   &nbsp; &nbsp; 0 0 0 0 0 0 </br>
   &nbsp; &nbsp; 0 0 0 0 0 0 </br>

both have the same structures as the triggers with 4 observed variables when we ignore the single disconnected node in these two triggers. You can modify the code (by changing the value of "allow_disconnected_nodes" in MainClass.java) and see the difference. In most cases, we do not care any structures with disconnected nodes.

We also implemented a multi-threads version (https://github.com/zxh298/Trigger_threads) to search for up to 7 nodes, which runs much faster. Searching triggers for greater than 7 nodes is not necessary, as we found triggers with 5, 6 or 7 nodes all have either one of the 4 node triggers as a subnet. Due to the time limit, we did not search triggers beyond 7 nodes, as we assume all triggers have either one of the 4 node triggers as a subnet.

See more details in our publication, which is also the primary publication to site for Trigger program is::

Zhang, X., Korb, K. B., Nicholson, A. E. and Mascaro, S. (2017). Applying dependency patterns in causal discovery of latent variable models, Artificial Life and Computational Intelligence, Springer International Publishing, Cham, pp. 134–143.

@InProceedings{zhang2017dependency, </br>
   &nbsp; &nbsp; author="Zhang, Xuhui and Korb, Kevin B. and Nicholson, Ann E. and Mascaro, Steven", </br>
   &nbsp; &nbsp; title="Applying Dependency Patterns in Causal Discovery of Latent Variable Models", </br>
   &nbsp; &nbsp; booktitle="Artificial Life and Computational Intelligence", </br>
   &nbsp; &nbsp; year="2017", </br>
   &nbsp; &nbsp; publisher="Springer International Publishing", </br>
   &nbsp; &nbsp; address="Cham", </br>
   &nbsp; &nbsp; pages="134--143", </br>
   &nbsp; &nbsp; isbn="978-3-319-51691-2" </br>
} 

You must to cite our publication for any purpose.



