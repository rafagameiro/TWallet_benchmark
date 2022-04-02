#!/usr/bin/env python

from os import walk
import sys, getopt
import json
import numpy as np
import pandas as pd  
import matplotlib.pyplot as plt

#------------------------------------- Process Attestation ---------------------------------------

def draw_scatterplot(x, y, names, xlabel, ylabel):

    plt.rcParams['ytick.major.pad']='20'
    plt.rcParams['axes.axisbelow'] = True
    plt.grid(zorder=0, which='both')
    plt.minorticks_on()

    max_value = 0
    # plotting the points 
    for name in names:
        if max_value < int(max(y[name])):
            max_value = int(max(y[name]))
        plt.scatter(x[name], y[name], label=name)
        plt.plot(x[name], y[name])

    plt.yticks(np.arange(0, max_value, 1000))
    # naming the axis
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)

    plt.legend()
    # giving a title to my graph
    plt.show()

def process_attestation(directory):
        
    filenames = next(walk(directory), (None, None, []))[2]
    filenames.sort()
    
    files_data_x = {}
    files_data_y = {}
    files_names = []
    filenames = sorted(filenames, key=lambda x: int(x.split('-')[2]))
    print (filenames)
    for filename in filenames:
        data, _ = load_file(directory + filename)
        name = filename.split("-")[1]
        if name not in files_data_x:
            files_data_x[name] = []
            files_data_y[name] = []
        
        files_data_y[name].append(data)
        files_data_x[name].append(filename.split("-")[2])
        if name not in files_names:
            files_names.append(filename.split("-")[1])
    
    draw_scatterplot(files_data_x, files_data_y, files_names, "Key Sizes", "Latency (ms)")

#------------------------------------- Process Resources ---------------------------------------

def load_points(filename, scale=1.0):
    f = open(filename,)

    data = json.load(f)

    f.close()
    
    return data["points"]

def combine_values(temp_data):
    
    data = {}
    temp_array = []
    for key in temp_data.keys():
        data[key] = {}
        for tp in temp_data[key].keys():
            temp_array = []
            if len(temp_data[key][tp]) > 1:
                for x in range(len(temp_data[key][tp]) - 1):
                    if x == 0:
                        temp_array = np.add(temp_data[key][tp][x], temp_data[key][tp][x+1])
                    else:
                        temp_array = np.add(temp_array, temp_data[key][tp][x+1])
            else:
                temp_array = np.array(temp_data[key][tp][0])

            (data[key])[tp] = temp_array/len(temp_data[key][tp])

    return data

def draw_areaplot(x, y, names, y_label):

    colors = []
    
    if "Memory" in y_label:
        colors = ['#6666ff', '#0000cc']
    elif "CPU" in y_label:
        colors = ['#39e600', '#248f24']
    
    i = 0
    plt.rcParams.update({'font.size': 15})
    for name in names:
        y[i].plot(kind='area', stacked=False, color=colors)
        i = i + 1
        # naming the axis
        plt.xlabel('Time (s)')
        plt.ylabel(y_label)
        plt.ylim([0, 100])
        plt.legend(loc='upper right', prop={'size': 15})
        # giving a title to my graph
        plt.show()

def draw_lineplot(x, y, names, y_label):

    types = ['twallet', 'normal']
    linestyles = ['--', ':'] 
    colors = []
    if "Memory" in y_label:
        colors = ['#6666ff', '#0000cc']
    elif "CPU" in y_label:
        colors = ['#39e600', '#248f24']
    
    i = 0
    print(x)
    print(y)
    plt.rcParams.update({'font.size': 15})
    fig, axs = plt.subplots(1, 3, sharey=True, gridspec_kw={'wspace': 0.05}) 
    fig.text(0.5, 0.04,'Time (s)', va='center', ha='center')
    fig.text(0.08, 0.5, y_label, va='center', ha='center', rotation='vertical')
    for name in names:
        axs[i].plot(x[name], y[i].loc[:, "twallet"], label="twallet", color="#000000", linestyle='--')
        axs[i].plot(x[name], y[i].loc[:, "normal"], label="normal", color="#000000")
        i = i + 1
        # naming the axis
    plt.ylim([0, 400])
    plt.legend(loc='upper right', prop={'size': 15})
    # giving a title to my graph
    plt.show()

def process_resources(directory):
        
    filenames = next(walk(directory), (None, None, []))[2]
    filenames.sort()
    
    files_data_x = {}
    files_data_y = []
    temp_data_y = {}
    files_names = []
    max_range = 0
    y_label = ""

    if "cpu" in directory:
        max_range = 19
        y_label = "CPU (%)"
    elif "memory" in directory:
        #max_range = 7 #for memory_1
        max_range = 7 #for memory_2
        y_label = "Memory (MB)"

    for filename in filenames:
        data = load_points(directory + filename)
        name = filename.split("-")[1]
        tp = filename.split("-")[2]
        if name not in files_data_x:
            temp_data_y[name] = {}
            files_data_x[name] = range(1,max_range)
            
        if tp not in temp_data_y[name]:
            (temp_data_y[name])[tp] = [] 
        (temp_data_y[name])[tp].append(data) 
        if name not in files_names:
            files_names.append(name)
    
    temp_data_y = combine_values(temp_data_y)
    for name in files_names:
        files_data_y.append(pd.DataFrame(temp_data_y[name], columns=['twallet', 'normal']))
   
    #draw_areaplot(files_data_x, files_data_y, files_names, y_label)
    draw_lineplot(files_data_x, files_data_y, files_names, y_label)

#------------------------------------- Compare Files ---------------------------------------

def process_metrics(args):

    if "network" in args[0]:
        if len(args) > 1:
            print_help()

        return "network", [20, 10]

    if "operations" in args[0]:
        if len(args) != 3:
            print_help()

        return "performance", [args[1], args[2]]

    if "custom" in args[0]:
        if len(args) != 3:
            print_help()

        return "performance", [args[1], args[2]]

def load_file(filename, scale=1.0):
    f = open(filename,)

    data = json.load(f)
    data_final = []

    for value in data["values"]:
        data_final.append(value / scale)

    f.close()
    
    return np.mean(data_final) , np.std(data_final)

def draw_plot(x, y, error, plot_type, xlabel, ylabel):

    # plotting the points 
    plt.rcParams.update({'font.size': 15})
    if plot_type == "network":
        # Networking
        ax = x.plot(kind='bar', yticks=y, yerr=error, ecolor='black', capsize=10, align='center', color=['#00bfff', '#002699'], rot=0)
    else:
        #Performance
        ax = x.plot(kind='bar', yticks=y, yerr=error, ecolor='black', capsize=10, align='center', color=['#B8B8B8', '#808080', '#505050'], rot=0, fontsize=15)
         
    ax.set_axisbelow(True)

    plt.grid(zorder=0, which='both')
    plt.minorticks_on()
    # naming the axis
    #plt.xlabel(xlabel)
    plt.ylabel(ylabel, fontsize=12)
    plt.yticks(fontsize=10)
    plt.legend(prop={'size': 15})                              
    plt.show()

def compare_files(directory, args):
# To plot: create, load, and transaction, use [+ 4000, 1000]
#
# To plot: delete, balance, history, use [+ 200, 100]
#
# To plot: network, use [+20, 10]
   
    plot_type, metrics = process_metrics(args);

    filenames = next(walk(directory), (None, None, []))[2]
    filenames.sort()

    d_file = {}
    data_index = []
    for filename in filenames:
        x = filename.split("-")
        if x[1] not in data_index:
            if "balance" in x[1] and "Balance" not in data_index:
                data_index.append(x[1].capitalize())
            elif "transaction" in x[1] and "Send Transaction" not in data_index:
                data_index.append("Send Transaction")
            elif "history" in x[1] and "History of Transactions" not in data_index:
                data_index.append("History of Transactions")
            elif "create" in x[1] and "Create Wallet" not in data_index:
                data_index.append("Create Wallet")
            elif "load" in x[1] and "Load Wallet" not in data_index:
                data_index.append("Load Wallet")
            elif "delete" in x[1] and "Delete Wallet" not in data_index:
                data_index.append("Delete Wallet")
        if x[2] not in d_file:
            d_file[x[2]] = []
        d_file[x[2]].append(filename)
    
    d_value = {}
    max_value = []
    data_error = {}
    for key in d_file.keys():
        d_value[key] = []
        data_error[key] = []
        for file in d_file[key]:
            data, error = load_file(directory + file)
            d_value[key].append(data)
            data_error[key].append(error)
        max_value.append(np.max(d_value[key]))
    
    data_y = np.arange(0, np.max(max_value) + int(metrics[0]), int(metrics[1]))
    data_x = pd.DataFrame(d_value, index = data_index)  
    draw_plot(data_x, data_y, data_error, plot_type, "Operations", "Latency (ms)")

#------------------------------------- Process Files ---------------------------------------

def draw_barplot(names, data, error, context_switch = 0, context_switch_error = 0):

    fig = plt.figure()
    
    # creating the bar plot
    plt.rcParams.update({'font.size': 15})
    plt.grid(zorder=0, which='both')
    plt.bar(names, context_switch, yerr=context_switch_error, edgecolor='black', align='center', ecolor='black', capsize=10, color ='#B8B8B8', width = 0.4, zorder=3)
    plt.bar(names, data, bottom=context_switch, yerr=error, edgecolor='black', align='center', ecolor='black', capsize=10, color ='#808080', width = 0.4, zorder=3)

    if context_switch != 0:
        if "Credentials" in names[0]:
            plt.legend(["Context Switch", "Authentication Service"], prop={'size':15})
        if "Data" in names[0]:
            plt.legend(["Context Switch", "Secure Storage"], prop={'size':15})
    plt.minorticks_on()
    plt.xlabel("Operations", fontsize=12)
    plt.ylabel("Latency (ms)", fontsize=12)
    plt.yticks(fontsize=10)
    plt.show()

def process_files(directory):
        
    filenames = next(walk(directory), (None, None, []))[2]
    files_data = []
    files_names = []
    files_error = []
    file_context_switch = ""

    for filename in filenames:
        if "context-switch" in filename:
            file_context_switch = filename
            filenames.remove(filename)

    for filename in filenames:
        data, error = load_file(directory + filename)
        files_data.append(data)
        if "authentication" in filename:
            files_names.append(filename.split("-")[1].capitalize() + " Credentials")
        elif "storage":
            files_names.append(filename.split("-")[1].capitalize() + " Data")
        else:
            files_names.append(filename.split("-")[1].capitalize())
        files_error.append(error)
    files_names = ['Logging: New Entry', 'Monitoring: Filter']
    if len(file_context_switch) > 0:
        data, error = load_file(directory + file_context_switch)
        temp_data = []
        for file in files_data:
            temp_data.append(file - data)
        files_data = temp_data

        draw_barplot(files_names, files_data, files_error, data, error)
    else:    
        draw_barplot(files_names, files_data, files_error)

def process_files_1(directory):
        
    filenames = next(walk(directory), (None, None, []))[2]
    files_data = []
    files_names = []
    files_error = []

    for filename in filenames:
        data, error = load_file(directory + filename)
        files_data.append(data)
        files_names.append(filename.split("-")[3].upper())
        files_error.append(error)

        draw_barplot(files_names, files_data, files_error, data, error)
    else:    
        draw_barplot(files_names, files_data, files_error)

#------------------------------------- Process Single File ---------------------------------------

def process_file(filename): 
    f = open(filename,)

    data = json.load(f)
 
    print("mean:")
    print(np.mean(data["values"]))    

    print("\nstandard deviation:")
    print((np.std(data["values"]) / np.mean(data["values"])) * 100)
    print(np.std(data["values"]))

    print("\nvalues:")
    print(data["values"]) 

#------------------------------------- Main Menu -------------------------------------------------

def print_help():

    print ("NAME")
    print ("\tscript.py - converts log time files into plots\n")

    print ("SYNOPSIS")
    print ("\tscript.py [OPTION]\n")

    print ("DESCRIPTION")
    print ("\t-a, --attestation")
    print ("\t\tprocess log files that contain times obtained from attestation process execution\n")
    
    print ("\t-r, --resources")
    print ("\t\tprocess log files that contain times obtained from resources measurements like CPU and Memory\n")
    
    print ("\t-c, --compare [compare-type]")
    print ("\t\tCompares files with same terminology, but performed in different environments\n")
    
    print ("\t-p, --process")
    print ("\t\tprocess log files so display a plot with all processed values\n")
    
    print ("\t-s, --stats")
    print ("\t\tprints statistics regarding the inputted file\n")
    sys.exit(1)

def main(argv):

    if len(argv) < 2:
        print_help()

    opts, args = getopt.getopt(argv,"ha:r:c:p:s:",["attestation","resource","compare","process=", "stats="])
    for opt, arg in opts:
        if opt == '-h':
            print_help()
            sys.exit()
        elif opt in ("-a", "--attestation"):
            process_attestation(arg)
        elif opt in ("-r", "--resources"):
            process_resources(arg)
        elif opt in ("-c", "--compare"):
            if len(args) != 1 and len(args) != 3:
                print_help()
            compare_files(arg, args)
        elif opt in ("-p", "--process"):
            process_files(arg)
        elif opt in ("-s", "--stats"):
            process_file(arg)


if __name__ == "__main__":
   main(sys.argv[1:])

