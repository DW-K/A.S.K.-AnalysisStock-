import pandas as pd

from Project_ASK.Path import RESULT_PATH_NEWS


def keywordToFirestore(category, companyName, target_date):
    output_file_name = f'{companyName}_{target_date}_nsc.xlsx'
    filePath = fr"{RESULT_PATH_NEWS}\{category}\{companyName}\news"
    output_path = fr"{filePath}\{output_file_name}"

    df =pd.read_excel(output_path, index_col="index", sheet_name="total")

