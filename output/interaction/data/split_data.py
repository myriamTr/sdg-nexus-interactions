import pandas as pd
import uuid

data = pd.read_csv('matrix_interactions.csv')

data = data[data["From SDG"].isin([3, 15, 16])]
data = data[data["To SDG "].isin([3, 15, 16])]

n, p = data.shape
splits = 2
m = int(n/splits) + 1

filenames = [str(uuid.uuid4()) for e in range(splits)]
dfs = []

# 3,15,16

filenames = \
    ['0332ca96-37ad-4e14-a925-b876708455cd',
     '88568137-59e5-4eff-88f9-f89a4ad2e310']

for i, filename in enumerate(filenames):
    df = data.iloc[(i*m):(i+1)*m]
    dfs.append(df)
    df.to_csv(filename)

# Check all are equals
print(all(pd.concat(dfs, axis=0) == data))
print(filenames)

# data = pd.read_csv("sdg_id_title.csv")
# data.to_json("sdg_id_title.json", orient='records')

# df = pd.concat([pd.read_csv(f) for f in filenames], axis=0)
# df = df.drop('Unnamed: 0', axis=1)
# index = df["Type"] == 'Own perception'
# df.loc[index, 'Geographical place'] = 'Kenya and Ethiopia'

# data = df
# n, p = data.shape
# splits = 2
# m = int(n/splits) + 1
# dfs = []
# for i, filename in enumerate(filenames):
#     df = data.iloc[(i*m):(i+1)*m]
#     dfs.append(df)
#     df.to_csv(filename)


# ['Geographical place'] = 'Kenya and Ethiopia'
