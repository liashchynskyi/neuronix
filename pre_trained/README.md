
# Pre-trained models

The following models have been trained for classification of cytological and histological images. 

## Cytology
The images consist of 6 classes:
1. *cancer* - cancer
2. *cyt_fibrozno_kistozna_mastopotia* - fibrocystic mastopathy
3. *cyt_kist_mast* - cystular mastopathy
4. *cyt_mast* - mastopathy
5. *cyt_nep_fib_mast* - nonproliferative fibrous mastopathy
6. *cyt_nep_mast* - nonproliferative mastopathy


| file| accuracy| precision|recall|f1 score| image size|
| ------------- |---------------| ------|----|---|---|
| [lenet_a85_p90_r83_f93_c6_is128_t7.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_a85_p90_r83_f93_c6_is128_t7.bin)      | 85%| 90%|83%|93%|128 px|
| [lenet(+c3x3)_a86_p90_r83_f93_c6_is128_t9.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet(+c3x3)_a86_p90_r83_f93_c6_is128_t9.bin)      | 86%| 90%|83%|93%|128 px|



## Histology
The images consist of 4 classes:
1. *histo_fibroadenoma* - fibroadenoma
2. *histo_fibrozno_kistozna_mastopatia* - fibrocystic mastopathy
3. *histo_lystovydna_fibroadenoma* - leaf-shaped fibroadenoma
4. *histo_neproliferatyvna_mastopatia* - nonproliferative mastopathy


| file| accuracy| precision|recall|f1 score| image size|
| ------------- |---------------| ------|----|---|---|
| [lenet_a100_p100_r100_f100_is128_t4.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_a100_p100_r100_f100_is128_t4.bin)      | 100%| 100%|100%|100%|128 px|



## Cytology (update)
The images consist of 5 classes:
1. *cyt_cancer* - cancer
2. *fibrocystic_mast* - fibrocystic mastopathy
3. *non_fibrocystic_mast* - nonproliferative **fibrous** mastopathy
4. *cet_mastopathy* - mastopathy
5. *nonproliferative_mast* - nonproliferative mastopathy


| file| accuracy| precision|recall|f1 score| image size|
| ------------- |---------------| ------|----|---|---|
| [lenet_00_a100_p100_r100_f100_is128_t3_c.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_00_a100_p100_r100_f100_is128_t3_c.bin)      | 100%| 100%|100%|100%|128 px|
| [lenet_01_a90_p93_r90_f89_is128_t3_c.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_01_a90_p93_r90_f89_is128_t3_c.bin)      | 90%| 93%|90%|89%|128 px|
| [lenet_02_a100_p100_r100_f100_is128_t2_c.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_02_a100_p100_r100_f100_is128_t2_c.bin)      | 100%| 100%|100%|100%|128 px|


## Histology (update)
The images consist of 5 classes:
1. *hist_proliferative_mast* - proliferative mastopathy
2. *histo_fibroadenoma* - fibroadenoma
3. *histo_fibrozno_kistozna_mastopatia* - fibrocystic mastopathy
4. *histo_lystovydna_fibroadenoma* - leaf-shaped fibroadenoma
5. *histo_neproliferatyvna_mastopatia* - nonproliferative mastopathy


| file| accuracy| precision|recall|f1 score| image size|
| ------------- |---------------| ------|----|---|---|
| [lenet_00_a90_p90_r90_f89_is128_t4_h.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_00_a90_p90_r90_f89_is128_t4_h.bin)      | 90%| 90%|90%|89%|128 px|
| [lenet_01_a92_p93_r93_f92_is128_t3_h.bin](https://github.com/liashchynskyi/neuronix/blob/master/pre_trained/lenet_01_a92_p93_r93_f92_is128_t3_h.bin)      | 92%| 93%|93%|92%|128 px|


