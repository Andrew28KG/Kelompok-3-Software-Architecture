import pandas as pd
import numpy as np
import os

import matplotlib.pyplot as plt

data_mvc = pd.read_csv('MVC_data.csv')
data_mvvm = pd.read_csv('MVVM_data.csv')

data_mvc['Architecture'] = 'MVC'
data_mvvm['Architecture'] = 'MVVM'

data = pd.concat([data_mvc, data_mvvm])

categories_columns = ['view_total', 'spin_total']
values_columns = ['time', 'memory']

colors = {'MVC': 'skyblue', 'MVVM': 'lightgreen'}

for category_column in categories_columns:
    for value_column in values_columns:
        plt.figure(figsize=(14, 8))
        grouped_data = {}

        for architecture in ['MVC', 'MVVM']:
            arch_data = data[data['Architecture'] == architecture]
            categories = arch_data[category_column]
            values = arch_data[value_column]
            grouped_data[architecture] = {category: [] for category in categories.unique()}
            for category, value in zip(categories, values):
                grouped_data[architecture][category].append(value)

        positions = np.arange(len(grouped_data['MVC'].keys())) * 2
        width = 0.35

        for i, (architecture, arch_grouped_data) in enumerate(grouped_data.items()):
            pos = positions + (i * width)
            bp = plt.boxplot(arch_grouped_data.values(),
                             positions=pos,
                             widths=width,
                             patch_artist=True,
                             labels=[f'{category}' for category in arch_grouped_data.keys()],
                             showfliers=False,
                             boxprops=dict(facecolor=colors[architecture], color='black'),
                             medianprops=dict(color='black'))

        plt.xlabel('Categories', fontsize=14)
        plt.title(f'Boxplot of {value_column.capitalize()} by {category_column.replace("_", " ").capitalize()}', fontsize=16)
        plt.ylabel(value_column.capitalize(), fontsize=14)

        plt.xticks(positions + width / 2, [f'{category}' for category in grouped_data['MVC'].keys()], rotation=45, ha='right', fontsize=12)

        plt.grid(True, linestyle='--', linewidth=0.5)

        handles = [plt.plot([], [], color=colors[architecture], label=architecture)[0] for architecture in colors.keys()]
        plt.legend(handles=handles, loc='center left', bbox_to_anchor=(1, 0.5), fontsize=12)

        for j, (architecture, arch_grouped_data) in enumerate(grouped_data.items()):
            for k, (category, data_values) in enumerate(arch_grouped_data.items()):
                median = np.median(data_values)
                q1 = np.percentile(data_values, 25)
                q3 = np.percentile(data_values, 75)
                pos = positions[k] + (j * width)
                offset = 0.05 * np.ptp(plt.ylim())

                plt.text(pos, q1 - offset, f'Q1={int(q1)}', ha='center', va='top', fontsize=9, color='blue')
                plt.text(pos, median, f'Median={int(median)}', ha='center', va='bottom', fontsize=9, color='black', weight='bold')
                plt.text(pos, q3 + offset, f'Q3={int(q3)}', ha='center', va='bottom', fontsize=9, color='green')

                avg = np.mean(data_values)
                plt.scatter(pos, avg, marker='x', color='red', zorder=5)
                plt.text(pos, avg + offset, f'Avg={int(avg)}', ha='center', va='bottom', fontsize=9, color='red')

        plt.tight_layout()
        plt.savefig(f'plot_{category_column}_{value_column}.pdf', bbox_inches='tight', pad_inches=0.2, dpi=300, format='pdf', transparent=True)
        plt.show()

print("Done!")
