import pandas as pd
import uuid

# data = pd.read_csv('matrix_interactions.csv')
# n, p = data.shape
# splits = 50
# m = int(n/splits) + 1

# filenames = [str(uuid.uuid4()) for e in range(splits)]
# dfs = []

# for i, filename in enumerate(filenames):
#     df = data.iloc[(i*m):(i+1)*m]
#     dfs.append(df)
#     df.to_csv(filename)

# # Check all are equals
# print(all(pd.concat(dfs, axis=0) == data))
# print(filenames)


# data = pd.read_csv("sdg_id_title.csv")
# data.to_json("sdg_id_title.json", orient='records')
