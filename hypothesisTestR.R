#John Kaiserlik
#CSC526 - Assignment (Hypothesis Testing)
#This is my own work. JRK. 3/25/2018
#This program will perform multiple 2-tailed, and unpaired t-tests.
#Apply Bonferroni Correction.
#NOTE TO GRADER****: The tsv file values were auto-truncated by read.table() down to the 4th decimal place or so.

rm(list=ls())
setwd('C:/Users/yo/Desktop/CSC526/Assignment3/')
file <- as.matrix(read.table('SemanticSimilarityScores.tsv', skip=1))

print('The following test consists of 2 two-sample, two-tailed, and paired t-tests with Bonferroni correction.\n');
print('If any of the p-values satisfies p-val <= alpha/m, where m=#tests, then declare significance and terminate any further tests.\n');
print('The null hypothesis states that there is no significant different difference between human and machine performance for 2 semantic similarity metrics(simJ and nic)\n\n')
n=463

simJ_Mach = 1:n
nic_Mach = 1:n
simJ_Huma = 1:n
nic_Huma = 1:n
diff_simJ = 1:n
diff_nic = 1:n
for (i in 1:n){
  # print(file[i,2])
  simJ_Mach[i] = file[i,2]
}
for (i in 1:n){
  # print(file[i,4])
  simJ_Huma[i] = file[i,4]
}
for (i in 1:n){
  # print(file[i,3])
  nic_Mach[i] = file[i,3]
}
for (i in 1:n){
  # print(file[i,5])
  nic_Huma[i] = file[i,5]
}

alpha = 0.05
corrected = 0.05/2
conf_lvl = 1-corrected


simJ_Result2 <- t.test(simJ_Mach, simJ_Huma, paired=TRUE, alternative="two.sided", conf.level = conf_lvl)
print(simJ_Result2)
if(simJ_Result2['p.value'] < corrected){
  sprintf("Reject the null hypothesis. Test shows significance at %f", corrected)
}

nic_Result3 <- t.test(nic_Mach, nic_Huma, paired=TRUE, alternative="two.sided", conf.level = conf_lvl)
print(nic_Result3)
if(nic_Result3['p.value'] < corrected){
  sprintf("Reject the null hypothesis. Test shows significance at %f", corrected)
}
file <- file('C:/Users/yo/Desktop/CSC526/Assignment3/results.txt')
c = simJ_Result2['statistic']
d = simJ_Result2['p.value']
e = nic_Result3['statistic']
f = nic_Result3['p.value']
str = paste("SIMJ test statistic: ", as.character(simJ_Result2['statistic']), "\n", "SIMJ p-value: ", as.character(simJ_Result2['p.value']), "\n")
str2 = paste("nic test statistic: ", as.character(nic_Result3['statistic']), "\n", "nic p-value: ", as.character(nic_Result3['p.value']))

writeLines(paste(str, str2), "C:/Users/yo/Desktop/CSC526/Assignment3/results.txt")
