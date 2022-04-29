import pandas as pd

if __name__ == "__main__":
    df = pd.read_excel(r'./d/fs.xlsx')

    # print(df.columns)

    columns = df.iloc[2:, 2]

    fs = df.iloc[2:, 8:].transpose()
    fs.index.name = 'date'
    fs.columns = columns
    # print(fs.columns)
    fs.to_excel(r'./d/fs2.xlsx')

