close all;
clear all;

%John Kaiserlik
%CSC526 - Assignment (Hypothesis Testing)
%This is my own work. JRK. 3/25/2018
%This program will perform multiple 2-tailed, and unpaired t-tests.
%Apply Bonferroni Correction.

%***Note to grader: May have to adjust the file path in line 9. 

file = tdfread('C:\Users\yo\Desktop\CSC526\Assignment3\SemanticSimilarityScores.tsv');
cols = fieldnames(file(1));
for i=1:numel(cols)
    val{i} = getfield(file(1), cols{i})
end

fprintf('The following test consists of 2 two-sample, two-tailed, and paired t-tests with Bonferroni correction.\n');
fprintf('If any of the p-values satisfies p-val <= alpha/m, where m=#tests, then declare significance and terminate any further tests.\n\n');


simJ_Mach = val{2};
simJ_Huma = val{4};
nic_Mach = val{3};
nic_Huma = val{5};
n = numel(simJ_Mach);
df = n - 1;

diff_simJ = zeros(n,1);
diff_nic = zeros(n,1);
parfor j=1:1:n
    diff_simJ(j, :) = abs(simJ_Huma(j,:) - simJ_Mach(j,:));
end
parfor j=1:1:n
    diff_nic(j, :) = abs(nic_Huma(j,:) - nic_Mach(j,:));
end
% disp(diff_simJ);
% disp('-------');
% disp(diff_nic);
%Calculate sample mean, sample standard deviation, and standard error.
mn_simJ = mean(diff_simJ);
fprintf('mean(diff_simJ): %d\n', mn_simJ);
mn_nic = mean(diff_nic);
fprintf('mean(diff_nic): %d\n', mn_nic);
stdv_simJ = std(diff_simJ);
fprintf('std(diff_simJ): %d\n', stdv_simJ);
stdv_nic = std(diff_nic);
fprintf('std(diff_nic): %d\n', stdv_nic);
se_simJ = stdv_simJ / sqrt(n);
se_nic = stdv_nic / sqrt(n);
fprintf('SE(diff_simJ): %d\n', se_simJ);
fprintf('SE(diff_nic): %d\n', se_nic);

%t = [exp(sample mean of differences) / Stand.Err)]
t_simJ = mn_simJ / se_simJ;
t_nic = mn_nic / se_nic;
fprintf('critical value t_simJ: %d\n', t_simJ);
fprintf('critical value t_nic: %d\n', t_nic);

term = 0;
Hnull = 'There is a significant difference in performance levels for humans and machines.';
Halt = 'There is a significant difference in performance levels for humans and machines.';
result = 'Not enough evidence to reject the null hypothesis.';
alpha = 0.05;
sig = alpha/2;
fprintf('alpha: %d\n', alpha);
fprintf('Bonferroni corrected significance level: %d\n\n', sig);

while term == 0
    %h=1 means reject the Ho.
    [h, p, ci, stats] = ttest(simJ_Mach, simJ_Huma, 'Alpha', sig);
    t_stat1 = getfield(stats(1), 'tstat');
    t_stat1Mag = abs(t_stat1);
    fprintf('SimJ p-val_{j=1}: %d,\n', p);
    fprintf('Test statistic: %d,\n', t_stat1);
    fprintf('|Test statistic|: %d,\n', t_stat1Mag);

    if p <= sig && h == 1
        result = 'Reject the null hypothesis,';
        fprintf('%d <= %d, \n%s \nTest shows significance at corrected threshhold level of %d\n\n', p, sig, result, sig);
        term = 1;
%         break;
    end

    [h2, p2, ci2, stats2] = ttest2(nic_Mach, nic_Huma, 'Alpha', sig);
    t_stat2 = getfield(stats2(1), 'tstat');
    t_stat2Mag = abs(t_stat2);

    fprintf('NIC p-val_{j=2}: %d,\n', p2);
    fprintf('Test statistic: %d,\n', t_stat2);
    fprintf('|Test statistic|: %d,\n', t_stat2Mag);

    if p2 <= sig && h2 == 1
        result = 'Reject the null hypothesis,';
        fprintf('%d <= %d, \n%s \nTest shows significance at corrected threshhold level of %d\n', p, sig, result, sig);
        term = 1;
%         break;
    end
end
if term == 0
    fprintf('%d !<= %d, \n%s \nTest does not show significance at corrected threshold level of %d\n', p, sig, result, sig);
end